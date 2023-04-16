package fun.qianxiao.originalassistant.appquery;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import fun.qianxiao.originalassistant.api.appquery.HLXAppQueryApi;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * HLXAppQuerier
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class HLXAppQuerier extends AppQuerier<HLXAppQueryApi> {
    private Scheduler scheduler;

    private void getAppInfoInJsonObject(JSONObject jsonObject, ApiQueryResponseListener.AnalysisResultInterface analysisResultInterface) {
        String appDesc = jsonObject.optString("appdesc").replace("<br />", "\n");
        appQueryResult.setAppIntroduction(appDesc);

        /* request app detail to get app pictures */
        long appId = jsonObject.optLong("appid");
        LogUtils.i("appId: " + appId);
        getApi().detail(appId)
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtils.i("onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try {
                            String data = responseBody.string();
                            JSONObject jsonObject1 = new JSONObject(data);
                            if (jsonObject1.optInt("status") == 1) {
                                JSONObject jsonObject2 = jsonObject1.optJSONObject("gameinfo");
                                if (jsonObject2 == null) {
                                    analysisResultInterface.onError(-1, "api request success but gameinfo jsonObject is null");
                                    return;
                                }
                                String[] pics = GsonUtils.fromJson(jsonObject2.optString("imageresource"), GsonUtils.getArrayType(String.class));
                                LogUtils.i("pics len: " + pics.length);
                                appQueryResult.setAppPictures(Arrays.asList(pics));

                                analysisResultInterface.getOnAppQueryListener().onResult(OnAppQueryListener.QUERY_CODE_SUCCESS, null, appQueryResult);
                            } else {
                                analysisResultInterface.onError(-1, "hlx api detail request success but status is not ok");
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            analysisResultInterface.onError(-1, "hlx api detail onNext " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        analysisResultInterface.onError(-1, "hlx api detail onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected @NonNull AppQuerier.ApiQueryResponseListener getApiQueryResponseListener() {
        return new ApiQueryResponseListener() {
            @Override
            public void onApiResponse(@NonNull ResponseBody response, AnalysisResultInterface analysisResultInterface) {
                try {
                    String data = response.string();
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.optInt("status") == 1) {
                        JSONArray jsonArray = jsonObject.optJSONArray("gameapps");
                        if (jsonArray == null) {
                            analysisResultInterface.onError(-1, "api request success but gameapps jsonArray is null");
                            return;
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            if (jsonObject1.optString("packname").equals(appQueryResult.getPackageName())) {
                                getAppInfoInJsonObject(jsonObject1, analysisResultInterface);
                                return;
                            }
                        }
                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(0);
                            getAppInfoInJsonObject(jsonObject1, analysisResultInterface);
                        }
                    } else {
                        analysisResultInterface.onError(-1, "hlx api status is not ok");
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    analysisResultInterface.onError(-1, e.getMessage());
                }
            }
        };
    }

    @Override
    protected Observable<ResponseBody> request(String appName, String packageName) {
        return getApi().query(appName);
    }

    @Override
    protected Scheduler getObserveThread() {
        if (scheduler == null) {
            scheduler = Schedulers.newThread();
        }
        return scheduler;
    }
}
