package fun.qianxiao.originalassistant.appquery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fun.qianxiao.originalassistant.api.appquery.TapTapAppQueryApi;
import fun.qianxiao.originalassistant.bean.AnalysisResult;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

/**
 * TapTapAppQuery
 *
 * @Author QianXiao
 * @Date 2023/4/18
 */
public class TapTapAppQuery extends AbstractAppQuerier<TapTapAppQueryApi, JSONObject> {
    @Override
    protected Observable<ResponseBody> search(String appName, String packageName) {
        return getApi().query(appName);
    }

    @Override
    protected Observable<ResponseBody> searchResponseAnalysisAndDetail(JSONObject searchResponseJsonObject, AnalysisResult analysisResult) {
        if (searchResponseJsonObject.optBoolean("success")) {
            JSONObject data = searchResponseJsonObject.optJSONObject("data");
            if (data == null) {
                return Observable.error(new Exception(analysisResult.getApi() + ": success but data is null"));
            }
            JSONArray list = data.optJSONArray("list");
            if (list == null) {
                return Observable.error(new Exception(analysisResult.getApi() + ": not found"));
            }
            if (list.length() == 0) {
                return Observable.error(new Exception(analysisResult.getApi() + ": success but data-list size is empty"));
            }
            JSONObject target = list.optJSONObject(0).optJSONObject("app");
            if (target == null) {
                return Observable.error(new Exception(analysisResult.getApi() + ": success but data-list[0]-app is null"));
            }
            for (int i = 0; i < list.length(); i++) {
                JSONObject jsonObject = list.optJSONObject(i);
                JSONObject app = jsonObject.optJSONObject("app");
                if (app == null) {
                    return Observable.error(new Exception(analysisResult.getApi() + ": success but data-list[" + i + "]-app is null"));
                }
                if (app.optString("identifier").equals(analysisResult.getAppQueryResult().getPackageName())) {
                    target = app;
                }
            }
            long appId = target.optLong("id");
            return getApi().detail(appId);
        } else {
            JSONObject data = searchResponseJsonObject.optJSONObject("data");
            if (data == null) {
                return Observable.error(new Exception(analysisResult.getApi() + ": not success and data is null"));
            }
            return Observable.error(new Exception(data.optString("msg")));
        }
    }

    @Override
    protected void detailResponseAnalysis(JSONObject detailResponseJsonObject, AnalysisResult analysisResult) {
        if (detailResponseJsonObject.optBoolean("success")) {
            JSONObject data = detailResponseJsonObject.optJSONObject("data");
            if (data == null) {
                analysisResult.setErrorMsg(analysisResult.getApi() + ": success but data is null");
                return;
            }
            JSONObject description = data.optJSONObject("description");
            if (description == null) {
                analysisResult.setErrorMsg(analysisResult.getApi() + ": success but description is null");
                return;
            }
            String descriptionText = description.optString("text").replace("<br/>", "\n");
            analysisResult.getAppQueryResult().setAppIntroduction(descriptionText);
            analysisResult.setSuccess(true);
            List<String> pics = new ArrayList<>();
            JSONArray screenshots = data.optJSONArray("screenshots");
            if (screenshots != null && screenshots.length() != 0) {
                for (int i = 0; i < screenshots.length(); i++) {
                    JSONObject jsonObject = screenshots.optJSONObject(i);
                    pics.add(jsonObject.optString("url"));
                }
            }
            analysisResult.getAppQueryResult().setAppPictures(pics);
        } else {
            JSONObject data = detailResponseJsonObject.optJSONObject("data");
            if (data == null) {
                analysisResult.setErrorMsg(analysisResult.getApi() + ": not success and data is null");
            } else {
                analysisResult.setErrorMsg(analysisResult.getApi() + ": " + data.optString("msg"));
            }
        }
    }
}
