package fun.qianxiao.originalassistant.api.translate;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * GoogleTranslateApi
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public interface GoogleTranslateApi {
    /**
     * translate to chinese
     *
     * @param en english text
     * @param tk tk
     * @return chinese result
     */
    @GET("http://translate.google.cn/translate_a/single?client=gtx&dt=t&dj=1&ie=UTF-8&sl=auto&tl=zh_CH")
    Observable<ResponseBody> translate(@Query("q") String en, @Query("tk") String tk);
}
