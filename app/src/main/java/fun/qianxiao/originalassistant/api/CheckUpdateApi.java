package fun.qianxiao.originalassistant.api;

import fun.qianxiao.originalassistant.config.AppConfig;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

/**
 * CheckUpdateApi
 *
 * @Author QianXiao
 * @Date long ago
 */
public interface CheckUpdateApi {

    /**
     * Check for updates to get updated configuration
     *
     * @return {@link Observable<ResponseBody>}
     */
    @GET(AppConfig.CHECKUPDATE_URL)
    Observable<ResponseBody> getUpdateConfig();
}
