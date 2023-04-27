package fun.qianxiao.originalassistant.api.appquery;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * HLCAppQueryApi
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public interface HLXAppQueryApi extends AppQueryaApi {
    String API_NAME = "HLX";

    /**
     * hlx search
     *
     * @param keyword keyword
     * @return
     */
    @GET("http://search.huluxia.com/game/search/ANDROID/4.1.5?start=0&count=20")
    Observable<ResponseBody> query(@Query("keyword") String keyword);

    /**
     * hlx app info detail
     *
     * @param appId app_id
     * @return
     */
    @GET("http://tools.huluxia.com/game/detail/ANDROID/4.1.5")
    Observable<ResponseBody> detail(@Query("app_id") long appId);
}
