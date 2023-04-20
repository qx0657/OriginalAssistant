package fun.qianxiao.originalassistant.manager;

import android.os.ConditionVariable;

import androidx.annotation.MainThread;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fun.qianxiao.originalassistant.api.hlx.HLXApi;
import fun.qianxiao.originalassistant.bean.HLXUserInfo;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.manager.net.ApiServiceManager;
import fun.qianxiao.originalassistant.other.SimpleObserver;
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
                .subscribeOn(Schedulers.newThread())
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
    public void getUserInfo(String key, String userId, OnGetUserInfoResult result) {
        if (!MyStringUtils.isNumeric(userId)) {
            result.onResult(false, null, "userId非法");
            return;
        }
        hlxApi.userInfo(key, Long.parseLong(userId))
                .subscribeOn(Schedulers.newThread())
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

    public interface OnSignInResult {
        /**
         * OnSignInResult onResult
         *
         * @param success success
         * @param errMsg  errMsg
         */
        void onResult(boolean success, String errMsg);
    }

    private String sign(Map<String, String> map) {
        String[] strArr = (String[]) map.keySet().toArray(new String[0]);
        Arrays.sort(strArr);
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : strArr) {
            stringBuilder.append(s);
            stringBuilder.append(map.get(s) == null ? "" : map.get(s));
        }
        stringBuilder.append(HLXApi.SIGN_SALT);
        return EncryptUtils.encryptMD5ToString(stringBuilder.toString()).toUpperCase(Locale.ROOT);
    }

    /**
     * Sign in
     *
     * @param key    key
     * @param catId  cat_id
     * @param result Callback
     */
    public void signIn(String key, int catId, OnSignInResult result) {
        long tsp = TimeUtils.getNowMills();
        Map<String, String> map = new HashMap<>();
        map.put("cat_id", String.valueOf(catId));
        map.put("time", String.valueOf(tsp));
        String sign = sign(map);
        hlxApi.signIn(key, catId, tsp, sign)
                .subscribeOn(Schedulers.newThread())
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

    /**
     * Check if the user has permission to post richly
     *
     * @param key    key
     * @param result result
     */
    public void hasRichPermission(@NonNull String key, OnCommonBooleanResultListener result) {
        hlxApi.hasRichPermission(key)
                .subscribeOn(Schedulers.newThread())
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

        void onUploadPicturesResult(int code, String errMsg, Map<File, String> result);
    }

    private Observable<ResponseBody> getUploadObservable(String key, File file) {
        Map<String, String> map = new HashMap<>();
        map.put("key", key);
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
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<List<String>>() {
            private String errMsg = null;

            @Override
            public List<String> doInBackground() throws Throwable {
                List<String> list = new ArrayList<>();
                Observable.interval(0, 10, TimeUnit.MILLISECONDS)
                        .map(aLong -> fileList.get(aLong.intValue()))
                        .take(fileList.size())
                        .flatMap((Function<File, ObservableSource<ResponseBody>>) file -> getUploadObservable(key, file))
                        .map(responseBody -> new JSONObject(responseBody.string()))
                        .doOnTerminate(conditionVariable::open)
                        .subscribe(new SimpleObserver<JSONObject>() {
                            @Override
                            public void onNext(@NonNull JSONObject jsonObject) {
                                if (jsonObject.optInt("status") == 1) {
                                    list.add(jsonObject.optString("url"));
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
            public void onSuccess(List<String> result) {
                if (result.size() == fileList.size()) {
                    Map<File, String> map = new LinkedHashMap<>();
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
}
