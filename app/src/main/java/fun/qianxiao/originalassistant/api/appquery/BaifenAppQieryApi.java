package fun.qianxiao.originalassistant.api.appquery;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * BaifenAppQieryApi
 *
 * @Author QianXiao
 * @Date 2023/4/19
 */
public interface BaifenAppQieryApi extends AppQueryaApi {
    String API_NAME = "Baifen";

    /**
     * baifen search
     *
     * @param keyword keyword
     * @return
     */
    @GET("https://app2.byfen.com/page_search_result?page=1")
    Observable<ResponseBody> query(@Query("keyword") String keyword);

    /**
     * baifen detail
     *
     * @param appId appId
     * @return
     */
    @GET("https://cache-app2.byfen.com/detail_id_lite2")
    Observable<ResponseBody> detail(@Query("id") long appId);
}
