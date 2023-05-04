package fun.qianxiao.originalassistant.manager;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fun.qianxiao.originalassistant.api.hlx.LinkedMeApi;
import fun.qianxiao.originalassistant.manager.net.ApiServiceManager;
import fun.qianxiao.originalassistant.utils.HLXUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Hulu Xia Deep Link Jump
 *
 * @Author QianXiao
 * @Date 2023/4/27
 */
public enum HLXLinkedMeManager {
    /**
     * singleton
     */
    INSTANCE;

    private final String KEY_FLOOR = "3f51cee222aabe71bdc1a748298003df";

    private final LinkedMeApi api;
    private String identityId;
    private String sessionId;
    private String schemeUrl;

    HLXLinkedMeManager() {
        api = ApiServiceManager.getInstance().create(LinkedMeApi.class);
    }

    /**
     * parse step 1 response and ready step 2(getUrl)
     *
     * @param initResponseBody initResponseBody
     * @param params           params
     * @return step 2 getUrl Observable
     */
    private Observable<ResponseBody> getUrl(ResponseBody initResponseBody, String params) {
        try {
            String res = initResponseBody.string();
            LogUtils.i(res);
            JSONObject jsonObject = new JSONObject(res);
            identityId = jsonObject.optString("identity_id");
            sessionId = jsonObject.optString("session_id");
            String sign = EncryptUtils.encryptMD5ToString(KEY_FLOOR + "&live&pc&live&live&" + params + "");
            Map<String, String> map = new HashMap<>();
            map.put("type", "live");
            map.put("feature", "live");
            map.put("stage", "live");
            map.put("channel", "pc");
            map.put("tags", "live");
            map.put("params", params);
            map.put("linkedme_key", KEY_FLOOR);
            map.put("session_id", sessionId);
            map.put("identity_id", identityId);
            map.put("source", "Web");
            map.put("sdk_version", "web1.0.2");
            map.put("timestamp", String.valueOf(TimeUtils.getNowMills()));
            map.put("sign", "");
            map.put("deeplink_md5", sign);
            map.put("deeplink_md5_new", sign);
            map.put("os", "Android");
            return api.getUrl(map);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * parse step 2 response and ready step 3(clickUrl)
     *
     * @param urlResponseBody urlResponseBody
     * @return step 3 clickUrl Observable
     */
    private Observable<ResponseBody> clickUrl(ResponseBody urlResponseBody) {
        try {
            String res = urlResponseBody.string();
            LogUtils.i(res);
            JSONObject jsonObject = new JSONObject(res);
            String url = jsonObject.optString("url");
            return api.clickUrl(url, Build.MODEL);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * parse step 3 response and ready step 4(close)
     * the response of step 4(close) is '{}', can ignore it
     *
     * @param clickUrlResponseBody clickUrlResponseBody
     * @return step 4 close Observable
     */
    private Observable<ResponseBody> parseSchemeUrl(ResponseBody clickUrlResponseBody) {
        try {
            String res = clickUrlResponseBody.string();
            LogUtils.i(res);
            JSONObject jsonObject = new JSONObject(res);
            long deeplinkId = jsonObject.optLong("deeplink_id");
            String deeplinkName = jsonObject.optString("deeplink_name");
            schemeUrl = HLXUtils.getHlxScheme() + "://linkedme?lkme=1&r=" + TimeUtils.getNowMills() + "&uid=" + deeplinkId + "&click_id=" + deeplinkName;
            return api.close("live", KEY_FLOOR, sessionId, identityId, String.valueOf(TimeUtils.getNowMills()));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getParamsJsonObject(JSONObject jsonObject) {
        JSONObject jsonObjectRes = new JSONObject();
        try {
            jsonObjectRes.put("$control", jsonObject);
            jsonObjectRes.put("$og_title", "DetailViewController");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectRes;
    }

    private void linkedMeJump(JSONObject jsonObject) {
        String params = getParamsJsonObject(jsonObject).toString();
        // step 1: init
        api.init(KEY_FLOOR)
                // step 2: getUrl
                .flatMap((Function<ResponseBody, ObservableSource<ResponseBody>>) responseBody -> getUrl(responseBody, params))
                // step 3: clickUrl
                .flatMap((Function<ResponseBody, ObservableSource<ResponseBody>>) this::clickUrl)
                // step 4: parseSchemeUrl and close
                .flatMap((Function<ResponseBody, ObservableSource<ResponseBody>>) this::parseSchemeUrl)
                .map(responseBody -> schemeUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        LogUtils.i(s);
                        ActivityUtils.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(s)));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShort("出错了");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * Jump to hlx app post detail page
     *
     * @param postId  post id
     * @param isVideo is video
     */
    public void gotoPostDetail(long postId, boolean isVideo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("JumpActivity", "TopicDetailActivity");
            jsonObject.put("TopicID", String.valueOf(postId));
            jsonObject.put("isVideo", isVideo ? "1" : "0");
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showShort("出错了");
            return;
        }
        linkedMeJump(jsonObject);
    }

    /**
     * Jump to hlx app activity detail page
     *
     * @param actionId  action id
     * @param extraInfo extra info
     */
    public void gotoActionDetail(long actionId, String extraInfo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("JumpActivity", "ActionDetailActivity");
            jsonObject.put("ActionID", String.valueOf(actionId));
            jsonObject.put("extraInfo", extraInfo);
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showShort("出错了");
            return;
        }
        linkedMeJump(jsonObject);
    }
}
