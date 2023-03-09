package fun.qianxiao.originalassistant.checkupdate;


import fun.qianxiao.originalassistant.config.AppConfig;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

public interface CheckUpdateApi {

    /**
     * 返回基于retrofit2的网络请求的被观察者
     *
     * @return 基于retrofit2的网络请求的被观察者
     */
    @GET(AppConfig.CHECKUPDATE_URL)
    Observable<ResponseBody> getUpdateConfig();
}
