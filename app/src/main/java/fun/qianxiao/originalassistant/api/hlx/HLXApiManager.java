package fun.qianxiao.originalassistant.api.hlx;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fun.qianxiao.originalassistant.bean.HLXUserInfo;
import fun.qianxiao.originalassistant.utils.net.ApiServiceManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * TODO
 *
 * @Author QianXiao
 * @Date 2023/3/14
 */
public enum HLXApiManager {
    /**
     * 单例
     */
    INSTANCE;

    public interface OnCheckKeyResult {
        /**
         * OnCheckKeyResult onResult
         *
         * @param valid  valid
         * @param errMsg error message
         */
        void onResult(boolean valid, String errMsg);
    }

    /**
     * Check whether the key is valid
     *
     * @param key key
     */
    public void checkKey(final String key, OnCheckKeyResult result) {
        ApiServiceManager.getInstance()
                .create(HLXApi.class)
                .checkKey(key)
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

    public interface OnGetUserInfoResult {
        /**
         * OnGetUserInfoResult onResult
         *
         * @param success     if success
         * @param hlxUserInfo {@link HLXUserInfo}
         */
        void onResult(boolean success, HLXUserInfo hlxUserInfo, String errMsg);
    }

    /**
     * Get user info
     *
     * @param key key
     */
    public void getUserInfo(String key, OnGetUserInfoResult result) {
        ApiServiceManager.getInstance()
                .create(HLXApi.class)
                .userInfo(key)
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
}
