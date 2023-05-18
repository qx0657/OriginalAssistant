package fun.qianxiao.originalassistant.manager.net;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * TimeOutTimeInterceptor
 * usage: @Headers({"CONNECT_TIMEOUT:600000", "READ_TIMEOUT:600000", "WRITE_TIMEOUT:600000"})
 *
 * @Author QianXiao
 * @Date 2023/5/19
 */
public class TimeOutTimeInterceptor implements Interceptor {
    public static final String CONNECT_TIMEOUT = "CONNECT_TIMEOUT";
    public static final String READ_TIMEOUT = "READ_TIMEOUT";
    public static final String WRITE_TIMEOUT = "WRITE_TIMEOUT";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        int connectTimeout = chain.connectTimeoutMillis();
        int readTimeout = chain.readTimeoutMillis();
        int writeTimeout = chain.writeTimeoutMillis();

        Request request = chain.request();
        String connectNew = request.header(CONNECT_TIMEOUT);
        String readNew = request.header(READ_TIMEOUT);
        String writeNew = request.header(WRITE_TIMEOUT);

        if (!TextUtils.isEmpty(connectNew)) {
            connectTimeout = Integer.parseInt(connectNew);
        }
        if (!TextUtils.isEmpty(readNew)) {
            readTimeout = Integer.parseInt(readNew);
        }
        if (!TextUtils.isEmpty(writeNew)) {
            writeTimeout = Integer.parseInt(writeNew);
        }

        return chain.withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .proceed(request);
    }
}
