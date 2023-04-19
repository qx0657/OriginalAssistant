package fun.qianxiao.originalassistant.manager;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import fun.qianxiao.originalassistant.api.hlx.HLXApi;
import fun.qianxiao.originalassistant.bean.HLXUserInfo;
import fun.qianxiao.originalassistant.manager.net.ApiServiceManager;
import fun.qianxiao.originalassistant.utils.MyStringUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * HLXApiManager
 *
 * @Author QianXiao
 * @Date 2023/3/14
 */
public enum HLXApiManager {
    /**
     * 单例
     */
    INSTANCE;

    private final String SIGN_SALT = "fa1c28a5b62e79c3e63d9030b6142e4b";

    public interface OnCheckKeyResult {
        /**
         * OnCheckKeyResult onResult
         *
         * @param valid  valid
         * @param errMsg error message
         */
        void onResult(boolean valid, String errMsg);
    }

    private void checkKey(HLXApi hlxApi, final String key, final String marketId, OnCheckKeyResult result) {
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
    public void checkKey(final String key, OnCheckKeyResult result) {
        HLXApi hlxApi = ApiServiceManager.getInstance()
                .create(HLXApi.class);
        checkKey(hlxApi, key, HLXApi.MARKET_ID_HLX_3L, new OnCheckKeyResult() {
            @Override
            public void onResult(boolean valid, String errMsg) {
                if (!valid) {
                    checkKey(hlxApi, key, HLXApi.MARKET_ID_HLX, result);
                } else {
                    result.onResult(valid, errMsg);
                }
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
        ApiServiceManager.getInstance()
                .create(HLXApi.class)
                .userInfo(key, Long.parseLong(userId))
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

    /**
     * Sign in
     *
     * @param key    key
     * @param catId  cat_id
     * @param result Callback
     */
    public void signIn(String key, int catId, OnSignInResult result) {
        long tsp = TimeUtils.getNowMills();
        String sign = EncryptUtils.encryptMD5ToString("cat_id" + catId + "time" + tsp + SIGN_SALT).toUpperCase(Locale.ROOT);
        ApiServiceManager.getInstance()
                .create(HLXApi.class)
                .signIn(key, catId, tsp, sign)
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
}
