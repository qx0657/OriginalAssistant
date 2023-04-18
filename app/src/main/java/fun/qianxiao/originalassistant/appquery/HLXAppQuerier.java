package fun.qianxiao.originalassistant.appquery;

import com.blankj.utilcode.util.GsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

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
public class HLXAppQuerier extends AppQuerier<HLXAppQueryApi> {

    @Override
    protected Observable<ResponseBody> search(String appName, String packageName) {
        return getApi().query(appName);
    }

    private Observable<ResponseBody> handleTargetJsonObjectAndDetail(JSONObject jsonObject, AnalysisResult analysisResult) {
        if (jsonObject == null) {
            return Observable.error(new Exception("hlx search targetJsonObject is null"));
        }
        String appDesc = jsonObject.optString("appdesc").replace("<br />", "\n");
        analysisResult.getAppQueryResult().setAppIntroduction(appDesc);

        long appId = jsonObject.optLong("appid");
        return getApi().detail(appId);
    }


    @Override
    protected Observable<ResponseBody> searchResponseAnalysisAndDetail(ResponseBody searchResponseBody, AnalysisResult analysisResult) {
        try {
            String data = searchResponseBody.string();
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.optInt("status") == 1) {
                JSONArray jsonArray = jsonObject.optJSONArray("gameapps");
                if (jsonArray == null) {
                    return Observable.error(new Exception("hlx api request success but gameapps jsonArray is null"));
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
                return Observable.error(new Exception("hlx api status is not ok"));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return Observable.error(e);
        }
    }

    @Override
    protected void detailResponseAnalysis(ResponseBody detailResponseBody, AnalysisResult analysisResult) {
        try {
            String data = detailResponseBody.string();
            JSONObject jsonObject1 = new JSONObject(data);
            if (jsonObject1.optInt("status") == 1) {
                JSONObject jsonObject2 = jsonObject1.optJSONObject("gameinfo");
                if (jsonObject2 == null) {
                    analysisResult.setErrorMsg("api request success but gameinfo jsonObject is null");
                    return;
                }
                String[] pics = GsonUtils.fromJson(jsonObject2.optString("imageresource"), GsonUtils.getArrayType(String.class));
                analysisResult.getAppQueryResult().setAppPictures(Arrays.asList(pics));
                analysisResult.setSuccess(true);
            } else {
                analysisResult.setErrorMsg("hlx api detail request success but status is not ok");
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            analysisResult.setErrorMsg("hlx api detail onNext " + e.getMessage());
        }
    }
}
