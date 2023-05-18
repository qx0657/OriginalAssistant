package fun.qianxiao.originalassistant.manager;

import android.os.ConditionVariable;

import androidx.annotation.IntRange;
import androidx.annotation.WorkerThread;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fun.qianxiao.originalassistant.api.hlx.HLXApi;
import fun.qianxiao.originalassistant.bean.FinalPostInfo;
import fun.qianxiao.originalassistant.bean.HLXUserInfo;
import fun.qianxiao.originalassistant.bean.PostStatisticsResult;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * HLXStatisticsManager
 *
 * @Author QianXiao
 * @Date 2023/5/14
 */
public class HLXStatisticsManager {
    private static final int GET_POST_LIST_EACH_COUNT_DEFAULT = 20;
    private static final Pattern TITLE_PREFIX_PATTERN = Pattern.compile("^【(.*?)】");

    private interface OnGetPostListListener {
        /**
         * onResult
         *
         * @param more              if has more
         * @param nextStart         next start
         * @param finalPostInfoList {@link List<FinalPostInfo>}
         */
        void onResult(boolean more, long nextStart, List<FinalPostInfo> finalPostInfoList);

        /**
         * onError
         *
         * @param errMsg error msg
         */
        void onError(String errMsg);
    }

    private static void getPostList(String key, long userId, long start, OnGetPostListListener result) {
        HLXApiManager.INSTANCE.getHlxApi().getPostList(key, userId, start, GET_POST_LIST_EACH_COUNT_DEFAULT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try {
                            String data = responseBody.string();
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.optInt("status", 0) == 1) {
                                JSONArray posts = jsonObject.optJSONArray("posts");
                                if (posts == null) {
                                    result.onError("posts array is null");
                                    return;
                                }
                                List<FinalPostInfo> finalPostInfoList = new ArrayList<>(GET_POST_LIST_EACH_COUNT_DEFAULT);
                                for (int i = 0; i < posts.length(); i++) {
                                    JSONObject post = posts.optJSONObject(i);
                                    FinalPostInfo finalPostInfo = new FinalPostInfo();

                                    finalPostInfo.setPostId(post.optLong("postID"));
                                    finalPostInfo.setTitle(post.optString("title"));
                                    // finalPostInfo.setDetail(post.optString("detail"));
                                    finalPostInfo.setScore(post.optInt("score"));
                                    finalPostInfo.setHit(post.optInt("hit"));
                                    finalPostInfo.setCommentCount(post.optInt("commentCount"));
                                    finalPostInfo.setGood(post.optInt("isGood") == 1);
                                    JSONObject category = post.optJSONObject("category");
                                    if (category != null) {
                                        finalPostInfo.getCategoryInfo().setCategoryId(category.optInt("categoryID"));
                                    }
                                    finalPostInfo.setImages(GsonUtils.fromJson(post.optString("images"), String[].class));

                                    finalPostInfoList.add(finalPostInfo);
                                }
                                /*
                                List<FinalPostInfo> finalPostInfoList = GsonUtils.fromJson(posts.toString(), new TypeToken<List<FinalPostInfo>>() {}.getType());
                                if (finalPostInfoList == null) {
                                    result.onError("gson parse posts array failed");
                                    return;
                                }
                                */
                                boolean more = jsonObject.optInt("more") == 1;
                                long start = jsonObject.optLong("start");
                                result.onResult(more, start, finalPostInfoList);
                            } else {
                                result.onError(jsonObject.optString("msg"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            result.onError(e.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        result.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface OnStatisticsListener {
        /**
         * onStart
         */
        void onStart();

        /**
         * onProgress
         *
         * @param percent percent, 0-100
         * @param msg     msg
         */
        void onProgress(@IntRange(from = 0, to = 100) int percent, String msg);

        /**
         * onSuccess
         *
         * @param postStatisticsResult {@link PostStatisticsResult}
         */
        void onSuccess(PostStatisticsResult postStatisticsResult);

        /**
         * onError
         *
         * @param errMsg error msg
         */
        void onError(String errMsg);

        /**
         * onComplete
         */
        void onComplete();
    }

    private static class SyncGetPostListResult {
        private String errMsg;
        private List<FinalPostInfo> finalPostInfoList;
        private boolean error;
        private boolean more;
        private long nextStart;

        public SyncGetPostListResult() {
        }

        public SyncGetPostListResult(List<FinalPostInfo> finalPostInfoList) {
            this.finalPostInfoList = finalPostInfoList;
            error = false;
        }

        public SyncGetPostListResult(String errMsg) {
            this.errMsg = errMsg;
            error = true;
        }

        public String getErrMsg() {
            return errMsg;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
            error = true;
        }

        public List<FinalPostInfo> getFinalPostInfoList() {
            return finalPostInfoList;
        }

        public void setFinalPostInfoList(List<FinalPostInfo> finalPostInfoList) {
            this.finalPostInfoList = finalPostInfoList;
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        public boolean isMore() {
            return more;
        }

        public void setMore(boolean more) {
            this.more = more;
        }

        public long getNextStart() {
            return nextStart;
        }

        public void setNextStart(long nextStart) {
            this.nextStart = nextStart;
        }
    }

    @WorkerThread
    private static SyncGetPostListResult syncGetPostList(String key, long userId, long start) {
        SyncGetPostListResult syncGetPostListResult = new SyncGetPostListResult();
        ConditionVariable conditionVariable = new ConditionVariable();
        getPostList(key, userId, start, new OnGetPostListListener() {
            @Override
            public void onResult(boolean more, long nextStart, List<FinalPostInfo> finalPostInfoList) {
                LogUtils.i(more, nextStart, finalPostInfoList);
                syncGetPostListResult.setMore(more);
                syncGetPostListResult.setNextStart(nextStart);
                syncGetPostListResult.setFinalPostInfoList(finalPostInfoList);
                conditionVariable.open();
            }

            @Override
            public void onError(String errMsg) {
                syncGetPostListResult.setErrMsg(errMsg);
                conditionVariable.open();
            }
        });
        if (!conditionVariable.block(3000)) {
            syncGetPostListResult.setErrMsg("TimeOut");
        }
        return syncGetPostListResult;
    }

    public static class StatisticParams {
        private int includeScrollThreshold = 100;
        private int postCount = -1;

        public int getIncludeScrollThreshold() {
            return includeScrollThreshold;
        }

        public void setIncludeScrollThreshold(int includeScrollThreshold) {
            this.includeScrollThreshold = includeScrollThreshold;
        }

        public int getPostCount() {
            return postCount;
        }

        public void setPostCount(int postCount) {
            this.postCount = postCount;
        }
    }

    /**
     * statistics hlx user post data
     *
     * @param key                  key
     * @param userId               user id
     * @param onStatisticsListener onStatisticsListener
     */
    public static void statisticsPostData(String key, long userId, StatisticParams params, OnStatisticsListener onStatisticsListener) {
        onStatisticsListener.onStart();
        if (params.getPostCount() != -1) {
            statisticInner(key, userId, params, onStatisticsListener);
        } else {
            HLXApiManager.INSTANCE.getUserInfo(key, userId, new HLXApiManager.OnGetUserInfoResult() {
                @Override
                public void onResult(boolean success, HLXUserInfo hlxUserInfo, String errMsg) {
                    if (success) {
                        params.setPostCount(hlxUserInfo.getPostCount());
                        statisticInner(key, userId, params, onStatisticsListener);
                    } else {
                        onStatisticsListener.onComplete();
                        onStatisticsListener.onError(errMsg);
                    }
                }
            });
        }
    }

    private static void statisticInner(String key, long userId, StatisticParams params, OnStatisticsListener onStatisticsListener) {
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                int postCount = params.postCount;
                if (postCount == 0) {
                    onStatisticsListener.onComplete();
                    onStatisticsListener.onError("您还没有发过帖子");
                    return null;
                }
                int hasGetPostCnt = 0;
                long nextStart = 0;
                SyncGetPostListResult syncGetPostListResult;
                PostStatisticsResult postStatisticsResult = new PostStatisticsResult();
                postStatisticsResult.setPostCount(postCount);
                do {
                    syncGetPostListResult = syncGetPostList(key, userId, nextStart);
                    if (syncGetPostListResult.error) {
                        onStatisticsListener.onComplete();
                        onStatisticsListener.onError(syncGetPostListResult.getErrMsg());
                        return null;
                    } else {
                        nextStart = syncGetPostListResult.getNextStart();
                        List<FinalPostInfo> finalPostInfoList = syncGetPostListResult.getFinalPostInfoList();
                        if (finalPostInfoList != null) {
                            hasGetPostCnt += finalPostInfoList.size();
                            for (FinalPostInfo finalPostInfo : finalPostInfoList) {
                                if (finalPostInfo.isGood()) {
                                    postStatisticsResult.addGoodPostCnt();
                                }
                                String prefix = getTitlePrefix(finalPostInfo.getTitle());
                                postStatisticsResult.addPrefixCount(prefix);
                                if (finalPostInfo.getCategoryInfo().getCategoryId() == HLXApi.CAT_ID_ORIGINAL) {
                                    postStatisticsResult.addOriginalCnt();
                                    postStatisticsResult.addPrefixOriginalCount(prefix);
                                    postStatisticsResult.addOriginalPostScore(finalPostInfo.getScore());
                                    if (finalPostInfo.getScore() >= params.getIncludeScrollThreshold()) {
                                        postStatisticsResult.addInclusionPostCnt();
                                    }
                                }
                            }
                        }
                        onStatisticsListener.onProgress(hasGetPostCnt * 100 / postCount, null);
                    }
                } while (syncGetPostListResult.isMore());
                onStatisticsListener.onComplete();
                onStatisticsListener.onSuccess(postStatisticsResult);
                return null;
            }

            @Override
            public void onSuccess(Boolean result) {

            }
        });
    }

    private static String getTitlePrefix(String title) {
        Matcher matcher = TITLE_PREFIX_PATTERN.matcher(title);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "其他";
        }
    }
}
