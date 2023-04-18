package fun.qianxiao.originalassistant.api.translate;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * BaiduTranslateApi
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public interface BaiduTranslateApi {
    String APP_ID = "20171229000110339";
    String SECRET = "DB70E832902436D308881BA0238B2C41E03DAC30067374AA3CC475F926997464";

    /**
     * translate to chinese
     *
     * @param en   english text
     * @param salt salt
     * @param sign sign
     * @return chinese result
     */
    @GET("http://api.fanyi.baidu.com/api/trans/vip/translate?from=auto&to=zh&appid=" + APP_ID)
    Observable<ResponseBody> translate(@Query("q") String en, @Query("salt") String salt, @Query("sign") String sign);
}
