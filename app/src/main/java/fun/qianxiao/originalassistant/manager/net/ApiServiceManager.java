package fun.qianxiao.originalassistant.manager.net;

import java.util.concurrent.TimeUnit;

import fun.qianxiao.originalassistant.config.AppConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ApiServiceManager
 *
 * @Author QianXiao
 * @Date long ago
 */
public class ApiServiceManager {
    private static final int DEFAULT_CONNECT_TIME = 5;
    private static final int DEFAULT_WRITE_TIME = 5;
    private static final int DEFAULT_READ_TIME = 5;
    private final Retrofit retrofit;
    private final OkHttpClient okHttpClient;

    public ApiServiceManager() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)
                .addInterceptor(new BasicParamsInterceptor.Builder()
                        .addHeaderLine("App: true")
                        .addHeaderLine("User-Agent: okhttp/4.9.3 APP_Original_Assistant")
                        .build())
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.APP_HOST)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    private static class SingletonHolder {
        private static final ApiServiceManager INSTANCE = new ApiServiceManager();
    }

    public static ApiServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
}
