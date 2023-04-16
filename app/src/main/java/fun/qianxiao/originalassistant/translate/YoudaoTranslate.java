package fun.qianxiao.originalassistant.translate;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.GsonUtils;

import org.json.JSONObject;

import java.util.Locale;
import java.util.UUID;

import fun.qianxiao.originalassistant.api.translate.YoudaoApi;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

/**
 * YoudaoTranslate
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class YoudaoTranslate extends Translate<YoudaoApi> {
    @Override
    protected void response(JSONObject jsonObject, OnTranslateListener onTranslateListener) {
        if (jsonObject.optInt("errorCode", 0) == 0) {
            String type = jsonObject.optString("l");
            if (type.endsWith("2zh-CHS")) {
                String[] translations = GsonUtils.fromJson(jsonObject.optString("translation"), GsonUtils.getArrayType(String.class));
                if (translations != null && translations.length > 0) {
                    String result = translations[0];
                    onTranslateListener.onTranslateResult(OnTranslateListener.TRANSLATE_SUCCESS, null, result);
                } else {
                    onTranslateListener.onTranslateResult(OnTranslateListener.TRANSLATE_ERROR,
                            "translations array in empty", null);
                }
            } else {
                String result = jsonObject.optString("query");
                onTranslateListener.onTranslateResult(OnTranslateListener.TRANSLATE_SUCCESS, null, result);
            }
        } else {
            onTranslateListener.onTranslateResult(OnTranslateListener.TRANSLATE_ERROR,
                    "errorcode is " + jsonObject.optInt("errorCode", 0), null);

        }
    }

    @Override
    protected Observable<ResponseBody> request(String text) {
        String salt = UUID.randomUUID().toString();
        String tsp = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = sign(text, salt, tsp);
        return getApi().translate(text, salt, sign, tsp);
    }

    private String sign(String text, String salt, String tsp) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(YoudaoApi.APP_KEY);
        if (text.length() > 20) {
            stringBuilder.append(text.substring(0, 10)).append(text.length()).append(text.substring(text.length() - 10));
        } else {
            stringBuilder.append(text);
        }
        stringBuilder.append(salt);
        stringBuilder.append(tsp);
        stringBuilder.append(YoudaoApi.SECRET);
        return EncryptUtils.encryptSHA256ToString(stringBuilder.toString()).toLowerCase(Locale.ROOT);
    }
}
