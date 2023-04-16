package fun.qianxiao.originalassistant.api.translate;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * YoudaoApi
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public interface YoudaoApi {
    String APP_KEY = "62b13f4b8adc66d0";
    String SECRET = "FWHPHZ21CzoBue5fkazrfmClguixUvkn";

    /**
     * translate to chinese
     *
     * @param text text
     * @return
     */
    @GET("https://openapi.youdao.com/api?from=from&to=zh-CHS&appKey=" + APP_KEY + "&signType=v3")
    Observable<ResponseBody> translate(@Query("q") String text, @Query("salt") String salt, @Query("sign") String sign, @Query("curtime") String curtime_s);
}
