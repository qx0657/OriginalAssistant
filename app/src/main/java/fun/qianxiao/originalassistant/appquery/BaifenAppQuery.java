package fun.qianxiao.originalassistant.appquery;

import com.blankj.utilcode.util.GsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import fun.qianxiao.originalassistant.api.appquery.BaifenAppQieryApi;
import fun.qianxiao.originalassistant.bean.AnalysisResult;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

/**
 * BaifenAppQuery
 *
 * @Author QianXiao
 * @Date 2023/4/19
 */
public class BaifenAppQuery extends AbstractAppQuerier<BaifenAppQieryApi, JSONObject> {
    @Override
    protected Observable<ResponseBody> search(String appName, String packageName) {
        return getApi().query(appName);
    }

    @Override
    protected Observable<ResponseBody> searchResponseAnalysisAndDetail(JSONObject searchResponse, AnalysisResult analysisResult) {
        int code = searchResponse.optInt("code");
        if (code == 1) {
            JSONObject data = searchResponse.optJSONObject("data");
            if (data == null) {
                return Observable.error(new Exception(analysisResult.getApi() + ": success but data is null"));
            }
            JSONArray list = data.optJSONArray("list");
            if (list == null) {
                return Observable.error(new Exception(analysisResult.getApi() + ": success but data-list is null"));
            }
            if (list.length() == 0) {
                return Observable.error(new Exception(analysisResult.getApi() + ": success but data-list is empty"));
            }
            JSONObject target = list.optJSONObject(0);
            for (int i = 0; i < list.length(); i++) {
                JSONObject jsonObject = list.optJSONObject(i);
                String packge = jsonObject.optString("packge");
                if (packge.equals(analysisResult.getAppQueryResult().getPackageName())) {
                    target = jsonObject;
                    break;
                }
            }
            long appId = target.optLong("id");
            return getApi().detail(appId);
        }
        return Observable.error(new Exception(analysisResult.getApi() + ": " + searchResponse.optString("msg", "code is " + code)));
    }

    @Override
    protected void detailResponseAnalysis(JSONObject detailResponseJsonObject, AnalysisResult analysisResult) {
        int code = detailResponseJsonObject.optInt("code");
        if (code == 1) {
            JSONObject data = detailResponseJsonObject.optJSONObject("data");
            if (data == null) {
                analysisResult.setErrorMsg(analysisResult.getApi() + ": success but data is null");
                return;
            }
            String content = data.optString("content");
            analysisResult.getAppQueryResult().setAppIntroduction(content);
            String[] pics = GsonUtils.fromJson(data.optString("gallery"), GsonUtils.getArrayType(String.class));
            analysisResult.getAppQueryResult().setAppPictures(pics);
            analysisResult.setSuccess(true);
        }
        analysisResult.setErrorMsg(analysisResult.getApi() + ": " + detailResponseJsonObject.optString("msg", "code is " + code));
    }
}
