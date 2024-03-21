package fun.qianxiao.originalassistant.manager;

import android.os.ConditionVariable;

import androidx.annotation.MainThread;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fun.qianxiao.originalassistant.api.hlx.HLXApi;
import fun.qianxiao.originalassistant.bean.FindBannerInfo;
import fun.qianxiao.originalassistant.bean.HLXUserInfo;
import fun.qianxiao.originalassistant.bean.PostResultInfo;
import fun.qianxiao.originalassistant.bean.UploadPictureResult;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.manager.net.ApiServiceManager;
import fun.qianxiao.originalassistant.other.AbstractSimpleObserver;
import fun.qianxiao.originalassistant.utils.HLXUtils;
import fun.qianxiao.originalassistant.utils.MyStringUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * HLXApiManager
 *
 * @Author QianXiao
 * @Date 2023/3/14
 */
public enum HLXApiManager {
    /**
     * singleton
     */
    INSTANCE;

    private final HLXApi hlxApi;

    HLXApiManager() {
        hlxApi = ApiServiceManager.getInstance().create(HLXApi.class);
    }

    public HLXApi getHlxApi() {
        return hlxApi;
    }

    public interface OnCommonBooleanResultListener {
        /**
         * OnCommonBooleanResultListener onResult
         *
         * @param valid  valid
         * @param errMsg error message
         */
        void onResult(boolean valid, String errMsg);
    }

    private void checkKey(final String key, final String marketId, OnCommonBooleanResultListener result) {
        hlxApi.checkKey(key, marketId)
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
                                result.onResult(true, null);
                            } else {
                                result.onResult(false, "key无效");
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            result.onResult(false, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        result.onResult(false, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * Check whether the key is valid
     *
     * @param key    key
     * @param result Callback
     */
    public void checkKey(final String key, OnCommonBooleanResultListener result) {
        checkKey(key, HLXApi.MARKET_ID_HLX_3L, (valid, errMsg) -> {
            if (!valid) {
                checkKey(key, HLXApi.MARKET_ID_HLX, (valid1, errMsg1) -> {
                    if (valid1) {
                        SPUtils.getInstance().put(SPConstants.KEY_HLX_KEY_MARKET_ID, HLXApi.MARKET_ID_HLX);
                    }
                    result.onResult(valid1, errMsg1);
                });
            } else {
                SPUtils.getInstance().put(SPConstants.KEY_HLX_KEY_MARKET_ID, HLXApi.MARKET_ID_HLX_3L);
                result.onResult(true, null);
            }
        });
    }

    public interface OnGetUserInfoResult {
        /**
         * OnGetUserInfoResult onResult
         *
         * @param success     if success
         * @param hlxUserInfo {@link HLXUserInfo}
         * @param errMsg      errMsg
         */
        void onResult(boolean success, HLXUserInfo hlxUserInfo, String errMsg);
    }

    /**
     * Get user info
     *
     * @param key    key
     * @param userId userId
     * @param result Callback
     */
    public void getUserInfo(String key, long userId, OnGetUserInfoResult result) {
        hlxApi.userInfo(key, userId)
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
                                HLXUserInfo userInfo = new HLXUserInfo();
                                userInfo.setUserId(jsonObject.optLong("userID"));
                                userInfo.setNick(jsonObject.optString("nick"));
                                userInfo.setAvatarUrl(jsonObject.optString("avatar"));
                                userInfo.setPostCount(jsonObject.optInt("postCount"));
                                userInfo.setCommentCount(jsonObject.optInt("commentCount"));
                                result.onResult(true, userInfo, null);
                            } else {
                                result.onResult(false, null, jsonObject.optString("msg"));
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            result.onResult(false, null, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        result.onResult(false, null, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface OnSignInResultListener {
        void onResult(boolean hasSign);

        void onError(String errMsg);
    }

    /**
     * signInCheck
     *
     * @param key    key
     * @param catId  catId
     * @param result result true if has not sign in
     */
    public void signInCheck(String key, int catId, OnSignInResultListener result) {
        hlxApi.signInCheck(key, catId)
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
                            if (jsonObject.optInt("status") == 1) {
                                if (jsonObject.optInt("signin") == 1) {
                                    result.onResult(true);
                                } else {
                                    result.onResult(false);
                                }
                            } else {
                                result.onError(jsonObject.optString("msg"));
                            }
                        } catch (JSONException | IOException e) {
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

    /**
     * Check if the user has permission to post richly
     *
     * @param key    key
     * @param result result
     */
    public void hasRichPermission(@NonNull String key, OnCommonBooleanResultListener result) {
        hlxApi.hasRichPermission(key)
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
                                if (jsonObject.optInt("isRich") == 1) {
                                    result.onResult(true, null);
                                } else {
                                    result.onResult(false, "无权限");
                                }
                            } else {
                                result.onResult(false, jsonObject.optString("msg"));
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            result.onResult(false, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        result.onResult(false, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface OnUploadPicturesListener {
        int UPLOAD_ALL_SUCCESS = 0;
        int UPLOAD_ERROR = -1;

        /**
         * onUploadPicturesResult
         *
         * @param code   code {@link OnUploadPicturesListener#UPLOAD_ALL_SUCCESS} if success
         * @param errMsg errMsg
         * @param result map of file and {@link UploadPictureResult}
         */
        void onUploadPicturesResult(int code, String errMsg, Map<File, UploadPictureResult> result);
    }

    private Observable<ResponseBody> getUploadObservable(String key, File file) {
        Map<String, String> map = new HashMap<>();
        map.put("_key", key);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("sign", HLXUtils.sign(map));
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody multipartBody = new MultipartBody.Builder().addFormDataPart("_key", "key_10")
                .addFormDataPart("file", file.getName(), requestBody)
                .build();
        return hlxApi.uploadPicture(map, multipartBody.parts());
    }

    /**
     * HLX uploads pictures
     * Upload multiple images in sequence, with an interval of 10ms.
     * If all images are successfully uploaded, it is considered successful.
     * When successful, callback the mapping of the file and url in {@link OnUploadPicturesListener#onUploadPicturesResult(int, String, Map)}
     *
     * @param key                      hlx_key
     * @param fileList                 file list
     * @param onUploadPicturesListener {@link OnUploadPicturesListener} callback
     */
    public void uploadPictures(String key, List<File> fileList, OnUploadPicturesListener onUploadPicturesListener) {
        ConditionVariable conditionVariable = new ConditionVariable(false);
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<List<UploadPictureResult>>() {
            private String errMsg = null;

            @Override
            public List<UploadPictureResult> doInBackground() throws Throwable {
                List<UploadPictureResult> list = new ArrayList<>();
                Observable.interval(0, 10, TimeUnit.MILLISECONDS)
                        .map(aLong -> fileList.get(aLong.intValue()))
                        .take(fileList.size())
                        .flatMap((Function<File, ObservableSource<ResponseBody>>) file -> getUploadObservable(key, file))
                        .map(responseBody -> new JSONObject(responseBody.string()))
                        .doOnTerminate(conditionVariable::open)
                        .subscribe(new AbstractSimpleObserver<JSONObject>() {
                            @Override
                            public void onNext(@NonNull JSONObject jsonObject) {
                                if (jsonObject.optInt("status") == 1) {
                                    UploadPictureResult uploadPictureResult = new UploadPictureResult();
                                    uploadPictureResult.setFid(jsonObject.optString("fid"));
                                    uploadPictureResult.setUrl(jsonObject.optString("url"));
                                    list.add(uploadPictureResult);
                                } else {
                                    onError(new Throwable(jsonObject.optString("msg")));
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                errMsg = e.getMessage();
                                conditionVariable.open();
                            }
                        });
                conditionVariable.block();
                return list;
            }

            @MainThread
            @Override
            public void onSuccess(List<UploadPictureResult> result) {
                if (result.size() == fileList.size()) {
                    Map<File, UploadPictureResult> map = new LinkedHashMap<>();
                    for (int i = 0; i < result.size(); i++) {
                        map.put(fileList.get(i), result.get(i));
                    }
                    onUploadPicturesListener.onUploadPicturesResult(OnUploadPicturesListener.UPLOAD_ALL_SUCCESS, null, map);
                } else {
                    onUploadPicturesListener.onUploadPicturesResult(OnUploadPicturesListener.UPLOAD_ERROR, errMsg, null);
                }
            }
        });
    }

    public interface OnPostListener {
        /**
         * post onSuccess
         *
         * @param postResultInfo {@link PostResultInfo}
         */
        void onSuccess(PostResultInfo postResultInfo);

        /**
         * post onError
         *
         * @param code   code
         * @param errMsg errMsg
         */
        void onError(int code, String errMsg);
    }

    /**
     * post
     *
     * @param key    hlx_key
     * @param title  post title
     * @param detail post detail
     * @param images images, for no rich it's split by ',', such as 'g4/M01/81/5C/rBAAdmREwDyAEC3NAAHdrncekLc863.jpg,g4/M01/81/5C/rBAAdmREwDyAZSQAAADSG9V436g35.jpeg,'
     * @param result resultListener
     */
    public void post(String key, String title, String detail, String images, boolean isRich, OnPostListener result) {
        Map<String, String> postMap = new HashMap<>();
        postMap.put("title", title);
        postMap.put("detail", detail);
        postMap.put("images", images);
        postMap.put("device_code", HLXUtils.getDeviceCode());
        postMap.put("_key", key);
        postMap.put("voice", "");
        postMap.put("sign", HLXUtils.sign2(postMap));
        postMap.put("cat_id", String.valueOf(HLXApi.CAT_ID_ORIGINAL));
        postMap.put("tag_id", String.valueOf(HLXApi.TAG_ID_ORIGINAL));
        postMap.put("type", "0");
        postMap.put("patcha", "");
        postMap.put("lng", "0.0");
        postMap.put("lat", "0.0");
        postMap.put("user_ids", "");
        postMap.put("recommendTopics", "");
        postMap.put("is_app_link", isRich ? "4" : "3");

        hlxApi.post(key, postMap)
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
                                PostResultInfo postResultInfo = new PostResultInfo();
                                postResultInfo.setPostId(jsonObject.optLong("postID"));
                                postResultInfo.setCode(jsonObject.optInt("code"));
                                postResultInfo.setMsg(jsonObject.optString("msg"));
                                result.onSuccess(postResultInfo);
                            } else {
                                result.onError(-1, jsonObject.optString("msg"));
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            result.onError(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        result.onError(-1, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface OnGetActivityListListener {
        int SUCCESS = 0;
        int FAILED = -1;

        /**
         * onGetActivityList
         *
         * @param code   code {@link OnGetActivityListListener#SUCCESS} or {@link OnGetActivityListListener#FAILED}
         * @param errMsg error message
         * @param list   {@link List<FindBannerInfo>}
         */
        void onGetActivityList(int code, String errMsg, List<FindBannerInfo> list);
    }

    /**
     * Get activity list of ongoing states from hlx.
     *
     * @param listListener {@link OnGetActivityListListener}
     */
    public void getActivityList(OnGetActivityListListener listListener) {
        hlxApi.activityList()
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
                                JSONArray jsonArray = jsonObject.optJSONArray("list");
                                if (jsonArray != null) {
                                    List<FindBannerInfo> list = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                        if (jsonObject1.optInt("activityStatus") == 1) {
                                            FindBannerInfo findBannerInfo = new FindBannerInfo(
                                                    jsonObject1.optLong("id"),
                                                    jsonObject1.optString("picture_url"),
                                                    jsonObject1.optString("title")
                                            );
                                            String jump_mode = jsonObject1.optString("jump_mode");
                                            if (jump_mode.startsWith("http")) {
                                                findBannerInfo.setMode(FindBannerInfo.MODE.URL);
                                                findBannerInfo.setUrl(jump_mode);
                                            } else if (MyStringUtils.isNumeric(jump_mode)) {
                                                findBannerInfo.setMode(FindBannerInfo.MODE.POST);
                                                findBannerInfo.setPostId(Long.parseLong(jump_mode));
                                            }
                                            list.add(findBannerInfo);
                                        }
                                    }
                                    listListener.onGetActivityList(OnGetActivityListListener.SUCCESS, null, list);
                                } else {
                                    listListener.onGetActivityList(OnGetActivityListListener.FAILED, "activityList success but list jsonArray is null", null);
                                }
                            } else {
                                listListener.onGetActivityList(OnGetActivityListListener.FAILED, jsonObject.optString("msg"), null);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            listListener.onGetActivityList(OnGetActivityListListener.FAILED, e.getMessage(), null);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listListener.onGetActivityList(OnGetActivityListListener.FAILED, e.getMessage(), null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface OnGetCatListListener {
        /**
         * onResult
         *
         * @param catList category map, <id, name>
         */
        void onResult(Map<Integer, String> catList);

        /**
         * onError
         *
         * @param errMsg error message
         */
        void onError(String errMsg);
    }

    /**
     * Get category list
     *
     * @param listListener onGetCatListListener
     */
    public void getCatList(OnGetCatListListener listListener) {
        boolean hidden = true;
        hlxApi.getCatList(hidden ? 1 : 0)
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
                                JSONArray categories = jsonObject.optJSONArray("categories");
                                if (categories != null) {
                                    Map<Integer, String> result = new LinkedHashMap<>();
                                    for (int i = 0; i < categories.length(); i++) {
                                        JSONObject category = categories.optJSONObject(i);
                                        if (category != null) {
                                            int categoryId = category.optInt("categoryID");
                                            String categoryName = category.optString("title");
                                            if (categoryId != 0 && categoryId != HLXApi.CAT_ID_ACTIVITY) {
                                                result.put(categoryId, categoryName);
                                            }
                                        }
                                    }
                                    listListener.onResult(result);
                                } else {
                                    listListener.onError("categories array is null");
                                }
                            } else {
                                listListener.onError(jsonObject.optString("msg"));
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            listListener.onError(e.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listListener.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
