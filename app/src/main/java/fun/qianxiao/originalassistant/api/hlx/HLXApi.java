package fun.qianxiao.originalassistant.api.hlx;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * HLXKeyApi
 *
 * @Author QianXiao
 * @Date 2023/3/14
 */
public interface HLXApi {
    /**
     * Check key
     *
     * @param key key
     * @return
     */
    @GET("http://floor.huluxia.com/account/token/check/ANDROID/2.1")
    Observable<ResponseBody> checkKey(@Query("session_key") String key);

    /**
     * Get user info
     *
     * @param key    key
     * @param userId userId
     * @return
     */
    @GET("http://floor.huluxia.com/user/info/ANDROID/4.1.8")
    Observable<ResponseBody> userInfo(@Query("_key") String key, @Query("user_id") String userId);
}
