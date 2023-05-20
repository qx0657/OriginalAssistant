package fun.qianxiao.originalassistant.manager;

import android.os.ConditionVariable;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import fun.qianxiao.originalassistant.utils.HLXUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * HLXSignInManager
 *
 * @Author QianXiao
 * @Date 2023/5/19
 */
public enum HLXSignInManager {
    /**
     * singleton
     */
    INSTANCE;

    public static final int SIGN_IN_MODE_FAST = 1;
    public static final int SIGN_IN_MODE_SAFE = 2;

    public static final int FAST_SIGN_IN_THREAD_NUM = 10;
    public static final int SAFE_MODE_INTERVAL_TIME = 500;

    private Map<Integer, String> successMap = new ConcurrentHashMap<>(37);
    private Map<Integer, String> failedMap = new ConcurrentHashMap<>();
    private Map<Integer, String> hasSignInMap = new ConcurrentHashMap<>();

    private AtomicInteger totalExpAdd = new AtomicInteger(0);
    private CountDownLatch countDownLatch;

    private ExecutorService executorService;

    public interface OnSignInResultListener {
        /**
         * onSuccess
         *
         * @param expAdd experience added
         */
        void onSuccess(int expAdd);

        /**
         * onHasSignIn
         */
        void onHasSignIn();

        /**
         * onError
         *
         * @param errMsg error message
         */
        void onError(String errMsg);
    }

    public interface OnSignResultListener {
        /**
         * onSuccess
         *
         * @param expAdd experience added
         */
        void onSuccess(int expAdd);

        /**
         * onError
         *
         * @param errMsg error message
         */
        void onError(String errMsg);
    }

    /**
     * Sign in
     *
     * @param key    key
     * @param catId  cat_id
     * @param result Callback
     */
    public void signIn(String key, int catId, OnSignResultListener result) {
        long tsp = TimeUtils.getNowMills();
        Map<String, String> map = new HashMap<>();
        map.put("cat_id", String.valueOf(catId));
        map.put("time", String.valueOf(tsp));
        String sign = HLXUtils.sign2(map);
        HLXApiManager.INSTANCE.getHlxApi().signIn(key, catId, tsp, sign)
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
                                result.onSuccess(jsonObject.optInt("experienceVal"));
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

    public interface OnSignInAllCategoryListener {
        /**
         * onStart
         */
        void onStart();

        /**
         * onResult
         *
         * @param successCatList successCatList
         * @param errorCatList   errorCatList
         * @param hasSignCatList hasSignCatList
         * @param expAdd         experience added
         */
        void onResult(Map<Integer, String> successCatList, Map<Integer, String> errorCatList, Map<Integer, String> hasSignCatList, int expAdd);

        /**
         * onProgress
         *
         * @param curCategory current category
         * @param pos         current position
         * @param total       total
         */
        void onProgress(Map.Entry<Integer, String> curCategory, int pos, int total);

        /**
         * onComplete
         */
        void onComplete();

        /**
         * onError
         *
         * @param errMsg error message
         */
        void onError(String errMsg);
    }

    /**
     * signInAllCategory
     *
     * @param key      key
     * @param mode     {@link HLXSignInManager#SIGN_IN_MODE_FAST} or {@link HLXSignInManager#SIGN_IN_MODE_SAFE}
     * @param listener listener
     */
    public void signInAllCategory(String key, int mode, OnSignInAllCategoryListener listener) {
        listener.onStart();
        HLXApiManager.INSTANCE.getCatList(new HLXApiManager.OnGetCatListListener() {
            @Override
            public void onResult(Map<Integer, String> catList) {
                if (mode == SIGN_IN_MODE_FAST) {
                    fastSignIn(key, catList, listener);
                } else if (mode == SIGN_IN_MODE_SAFE) {
                    safeSignIn(key, catList, listener);
                }
            }

            @Override
            public void onError(String errMsg) {
                listener.onComplete();
                listener.onError(errMsg);
            }
        });
    }

    private void fastSignIn(String key, Map<Integer, String> catMap, OnSignInAllCategoryListener listener) {
        ConcurrentLinkedDeque<Map.Entry<Integer, String>> concurrentLinkedDeque = new ConcurrentLinkedDeque<>(catMap.entrySet());

        if (executorService == null) {
            executorService = new ThreadPoolExecutor(FAST_SIGN_IN_THREAD_NUM, FAST_SIGN_IN_THREAD_NUM,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }

        successMap.clear();
        failedMap.clear();
        hasSignInMap.clear();
        totalExpAdd.set(0);
        countDownLatch = new CountDownLatch(catMap.size());

        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<MultiSignResult>() {
            @Override
            public MultiSignResult doInBackground() throws Throwable {
                for (int i = 0; i < FAST_SIGN_IN_THREAD_NUM; i++) {
                    executorService.submit(new Runnable() {
                        ConditionVariable conditionVariable = new ConditionVariable(false);

                        @Override
                        public void run() {
                            while (!concurrentLinkedDeque.isEmpty()) {
                                Map.Entry<Integer, String> catEntry = concurrentLinkedDeque.poll();
                                if (catEntry == null) {
                                    continue;
                                }
                                int catId = catEntry.getKey();
                                ThreadUtils.runOnUiThread(() -> listener.onProgress(catEntry, (int) (catMap.size() - countDownLatch.getCount() + 1), catMap.size()));
                                HLXApiManager.INSTANCE.signInCheck(key, catId, new HLXApiManager.OnSignInResultListener() {
                                    @Override
                                    public void onResult(boolean hasSign) {
                                        if (hasSign) {
                                            hasSignInMap.put(catId, catEntry.getValue());
                                            countDownLatch.countDown();
                                            conditionVariable.open();
                                        } else {
                                            signIn(key, catId, new OnSignResultListener() {
                                                @Override
                                                public void onSuccess(int expAdd) {
                                                    totalExpAdd.addAndGet(expAdd);
                                                    successMap.put(catId, catEntry.getValue());
                                                    countDownLatch.countDown();
                                                    conditionVariable.open();
                                                }

                                                @Override
                                                public void onError(String errMsg) {
                                                    failedMap.put(catId, catEntry.getValue());
                                                    countDownLatch.countDown();
                                                    conditionVariable.open();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onError(String errMsg) {
                                        failedMap.put(catId, catEntry.getValue());
                                        conditionVariable.open();
                                    }
                                });
                                conditionVariable.block();
                            }
                        }
                    });
                }
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MultiSignResult result = new MultiSignResult();
                result.successMap = successMap;
                result.failedMap = failedMap;
                result.hasSignInMap = hasSignInMap;
                result.expAdd = totalExpAdd.get();
                return result;
            }

            @Override
            public void onSuccess(MultiSignResult result) {
                listener.onComplete();
                listener.onResult(successMap, failedMap, hasSignInMap, totalExpAdd.get());
            }
        });

    }

    private void safeSignIn(String key, Map<Integer, String> catMap, OnSignInAllCategoryListener listener) {
        Map<Integer, String> successMap = new ConcurrentHashMap<>(catMap.size());
        Map<Integer, String> failedMap = new ConcurrentHashMap<>();
        Map<Integer, String> hasSignInMap = new ConcurrentHashMap<>();

        ConditionVariable conditionVariable = new ConditionVariable(false);

        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<MultiSignResult>() {
            @Override
            public MultiSignResult doInBackground() throws Throwable {
                int i = 0;
                final int[] totalExpAdd = {0};
                for (Map.Entry<Integer, String> cat : catMap.entrySet()) {
                    i++;
                    int catId = cat.getKey();
                    HLXApiManager.INSTANCE.signInCheck(key, catId, new HLXApiManager.OnSignInResultListener() {
                        @Override
                        public void onResult(boolean hasSign) {
                            if (hasSign) {
                                hasSignInMap.put(catId, cat.getValue());
                                conditionVariable.open();
                            } else {
                                signIn(key, catId, new OnSignResultListener() {
                                    @Override
                                    public void onSuccess(int expAdd) {
                                        successMap.put(catId, cat.getValue());
                                        totalExpAdd[0] += expAdd;
                                        conditionVariable.open();
                                    }

                                    @Override
                                    public void onError(String errMsg) {
                                        failedMap.put(catId, cat.getValue());
                                        conditionVariable.open();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(String errMsg) {
                            failedMap.put(catId, cat.getValue());
                            conditionVariable.block();
                        }
                    });
                    int finalI = i;
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onProgress(cat, finalI, catMap.size());
                        }
                    });
                    conditionVariable.block();
                    Thread.sleep(SAFE_MODE_INTERVAL_TIME);
                }
                MultiSignResult result = new MultiSignResult();
                result.successMap = successMap;
                result.failedMap = failedMap;
                result.hasSignInMap = hasSignInMap;
                result.expAdd = totalExpAdd[0];
                return result;
            }

            @Override
            public void onSuccess(MultiSignResult result) {
                listener.onComplete();
                listener.onResult(result.successMap, result.failedMap, result.hasSignInMap, result.expAdd);
            }
        });
    }

    static class MultiSignResult {
        Map<Integer, String> successMap;
        Map<Integer, String> failedMap;
        Map<Integer, String> hasSignInMap;
        int expAdd;
    }

    static class ProgressInfo {
        Map.Entry<Integer, String> curCategory;
        int pos;
        int total;
    }
}
