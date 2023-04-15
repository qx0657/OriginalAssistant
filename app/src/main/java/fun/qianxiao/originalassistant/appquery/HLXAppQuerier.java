package fun.qianxiao.originalassistant.appquery;

import fun.qianxiao.originalassistant.api.appquery.HLXAppQueryApi;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

/**
 * HLXAppQuerier
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class HLXAppQuerier extends AppQuerier {

    @Override
    protected @NonNull AppQuerier.ApiQueryResponseListener getApiQueryResponseListener() {
        return new ApiQueryResponseListener() {
            @Override
            public void onApiResponse(@NonNull ResponseBody response, AnalysisResultInterface analysisResultCallBack) {

                // TODO

                analysisResultCallBack.setIntroduction("");
                analysisResultCallBack.setPicture(null);

            }
        };
    }

    @Override
    protected Observable<ResponseBody> request(String appName, String packageName) {
        HLXAppQueryApi api = createApi(HLXAppQueryApi.class);

        // TODO

        return api.query();
    }
}
