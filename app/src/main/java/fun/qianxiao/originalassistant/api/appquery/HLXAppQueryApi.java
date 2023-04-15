package fun.qianxiao.originalassistant.api.appquery;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

/**
 * HLCAppQueryApi
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public interface HLXAppQueryApi {

    @GET("")
    Observable<ResponseBody> query(String... arg);
}
