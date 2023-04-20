package fun.qianxiao.originalassistant.api.hlx;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * HLXKeyApi
 *
 * @Author QianXiao
 * @Date 2023/3/14
 */
public interface HLXApi {
    String SIGN_SALT = "fa1c28a5b62e79c3e63d9030b6142e4b";
    String MARKET_ID_HLX_3L = "floor_web";
    String MARKET_ID_HLX = "tool_web";

    /**
     * Check key
     *
     * @param key      key
     * @param marketId marketId
     * @return {@link Observable<ResponseBody>}
     */
    @GET("http://floor.huluxia.com/account/token/check/ANDROID/2.1")
    Observable<ResponseBody> checkKey(@Query("session_key") String key, @Query("market_id") String marketId);

    /**
     * Get user info
     *
     * @param key    key
     * @param userId userId
     * @return {@link Observable<ResponseBody>}
     */
    @GET("http://floor.huluxia.com/user/info/ANDROID/4.1.8")
    Observable<ResponseBody> userInfo(@Query("_key") String key, @Query("user_id") long userId);

    /**
     * Sign in
     *
     * @param key   key
     * @param catId cat_id
     * @param tsp   tsp
     * @param sign  sign
     * @return {@link Observable<ResponseBody>}
     */
    @GET("http://floor.huluxia.com/user/signin/ANDROID/4.1.8")
    Observable<ResponseBody> signIn(@Query("_key") String key, @Query("cat_id") int catId, @Query("time") long tsp, @Query("sign") String sign);

    /**
     * hasRichPermission
     *
     * @param key key
     * @return {@link Observable<ResponseBody>}
     */
    @GET("http://floor.huluxia.com/create/power/ANDROID/2.3?cat_id=45&type_id=1")
    Observable<ResponseBody> hasRichPermission(@Query("_key") String key);

    @GET("http://floor.huluxia.com/post/create/ANDROID/4.2.2")
    Observable<ResponseBody> post(@Query("_key") String key);

    @Multipart
    @POST("http://upload.huluxia.com/upload/v3/image")
    Observable<ResponseBody> uploadPicture(@QueryMap Map<String, String> params, @Part List<MultipartBody.Part> file);
}
