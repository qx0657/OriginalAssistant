package fun.qianxiao.originalassistant.api.hlx;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Deep Link Jump Related LinkMe Request Interface Definition.
 * Used for deep link jump of Hulu Xia.
 *
 * @Author QianXiao
 * @Date 2023/4/27
 */
public interface LinkedMeApi {
    /**
     * step 1: init
     *
     * @param linkedMeKey linkedMeKey
     * @return {@link Observable<ResponseBody>}
     */
    @GET("https://lkme.cc/i/sdk/webinit?type=test")
    Observable<ResponseBody> init(@Query("linkedme_key") String linkedMeKey);

    /**
     * step 2: getUrl
     *
     * @param postMap postMap
     * @return {@link Observable<ResponseBody>}
     */
    @FormUrlEncoded
    @POST("https://lkme.cc/i/sdk/url")
    Observable<ResponseBody> getUrl(@FieldMap Map<String, String> postMap);

    /**
     * step 3: clickUrl
     *
     * @param uri        uri
     * @param deviceMode deviceMode
     * @return {@link Observable<ResponseBody>}
     */
    @GET("https://lkme.cc/i/sdk/click?os=Android&os_version=12&browser_name=WebView&browser_major=AppleWebKit")
    Observable<ResponseBody> clickUrl(@Query("uri") String uri, @Query("device_model") String deviceMode);

    /**
     * step 4: close
     *
     * @param type        type
     * @param linkedMeKey linkedMeKey
     * @param sessionId   sessionId
     * @param identityId  identityId
     * @param timestamp   timestamp
     * @return {@link Observable<ResponseBody>}
     */
    @FormUrlEncoded
    @POST("https://lkme.cc/i/sdk/webclose")
    Observable<ResponseBody> close(
            @Field("type") String type,
            @Field("linkedme_key") String linkedMeKey,
            @Field("session_id") String sessionId,
            @Field("identity_id") String identityId,
            @Field("timestamp") String timestamp
    );
}
