package fun.qianxiao.originalassistant.translate;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncryptUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

import fun.qianxiao.originalassistant.api.translate.BaiduTranslateApi;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

/**
 * BaiduTranslate
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class BaiduTranslate extends AbstractTranslate<BaiduTranslateApi> {
    @Override
    protected void response(JSONObject jsonObject, OnTranslateListener onTranslateListener) {
        if (!"0".equals(jsonObject.optString("error_code", "0"))) {
            onTranslateListener.onTranslateResult(OnTranslateListener.TRANSLATE_ERROR, jsonObject.optString("error_msg", "error_code is not 0"), null);
            return;
        }
        JSONArray jsonArray = jsonObject.optJSONArray("trans_result");
        if (jsonArray == null || jsonArray.length() == 0) {
            onTranslateListener.onTranslateResult(OnTranslateListener.TRANSLATE_ERROR, "trans_result jsonArray is null ? " + (jsonArray == null) + " or jsonArray.length() is 0", null);
            return;
        }
        JSONObject jsonObject1 = jsonArray.optJSONObject(0);
        String result = jsonObject1.optString("dst");

        onTranslateListener.onTranslateResult(OnTranslateListener.TRANSLATE_SUCCESS, null, result);
    }

    @Override
    protected Observable<ResponseBody> request(String text) {
        String salt = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = sign(text, salt);
        return getApi().translate(text, salt, sign);
    }

    private String sign(String text, String salt) {
        final String CRYPT_KEY_STR = "194ccc6f31cab0013d877cf0e84fc19f";
        final byte[] CRYPT_KEY = ConvertUtils.hexString2Bytes(CRYPT_KEY_STR);
        String secret = ConvertUtils.bytes2String(EncryptUtils.decryptHexStringAES(BaiduTranslateApi.SECRET, CRYPT_KEY, "AES/ECB/PKCS5Padding", null));
        String md5 = EncryptUtils.encryptMD5ToString(BaiduTranslateApi.APP_ID + text + salt + secret);
        return md5.toLowerCase(Locale.ROOT);

    }
}
