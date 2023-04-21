package fun.qianxiao.originalassistant.appquery;

import com.blankj.utilcode.util.GsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import fun.qianxiao.originalassistant.api.appquery.HLXAppQueryApi;
import fun.qianxiao.originalassistant.bean.AnalysisResult;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

/**
 * HLXAppQuerier
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class HLXAppQuerier extends AbstractAppQuerier<HLXAppQueryApi, JSONObject> {

    @Override
    protected Observable<ResponseBody> search(String appName, String packageName) {
        return getApi().query(appName);
    }

    private Observable<ResponseBody> handleTargetJsonObjectAndDetail(JSONObject jsonObject, AnalysisResult analysisResult) {
        if (jsonObject == null) {
            return Observable.error(new Exception(analysisResult.getApi() + ": search targetJsonObject is null"));
        }
        String appDesc = jsonObject.optString("appdesc").replace("<br />", "\n");
        analysisResult.getAppQueryResult().setAppIntroduction(appDesc);

        long appId = jsonObject.optLong("appid");
        return getApi().detail(appId);
    }

    @Override
    protected Observable<ResponseBody> searchResponseAnalysisAndDetail(JSONObject searchResponseJsonObject, AnalysisResult analysisResult) {
        if (searchResponseJsonObject.optInt("status") == 1) {
            JSONArray jsonArray = searchResponseJsonObject.optJSONArray("gameapps");
            if (jsonArray == null) {
                return Observable.error(new Exception(analysisResult.getApi() + ": hlx api request success but gameapps jsonArray is null"));
            }
            JSONObject targetJsonObject = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                if (jsonObject1.optString("packname").equals(analysisResult.getAppQueryResult().getPackageName())) {
                    targetJsonObject = jsonObject1;
                    break;
                }
            }
            if (targetJsonObject == null && jsonArray.length() > 0) {
                targetJsonObject = jsonArray.optJSONObject(0);
            }
            return handleTargetJsonObjectAndDetail(targetJsonObject, analysisResult);
        } else {
            return Observable.error(new Exception(analysisResult.getApi() + ": hlx api status is not ok"));
        }
    }

    @Override
    protected void detailResponseAnalysis(JSONObject detailResponseJsonObject, AnalysisResult analysisResult) {
        if (detailResponseJsonObject.optInt("status") == 1) {
            JSONObject jsonObject2 = detailResponseJsonObject.optJSONObject("gameinfo");
            if (jsonObject2 == null) {
                analysisResult.setErrorMsg(analysisResult.getApi() + ": api request success but gameinfo jsonObject is null");
                return;
            }
            String[] pics = GsonUtils.fromJson(jsonObject2.optString("imageresource"), GsonUtils.getArrayType(String.class));
            analysisResult.getAppQueryResult().setAppPictures(pics);
            analysisResult.setSuccess(true);
        } else {
            analysisResult.setErrorMsg(analysisResult.getApi() + ": hlx api detail request success but status is not ok");
        }
    }
}
