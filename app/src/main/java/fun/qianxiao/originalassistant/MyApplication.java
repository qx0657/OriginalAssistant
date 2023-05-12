package fun.qianxiao.originalassistant;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.hjq.http.EasyConfig;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.umeng.commonsdk.UMConfigure;

import fun.qianxiao.originalassistant.config.AppConfig;
import fun.qianxiao.originalassistant.other.RequestHandler;
import okhttp3.OkHttpClient;

/**
 * MyApplication
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class MyApplication extends Application {
    public static final String TAG = "OriginalAssistant";
    public static final String UMENGG_APP_ID = "6443937f4c2b215d8040d98b";
    public static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;

        initEasyHttp();
        initLogger();
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
    }

    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)
                .tag(TAG)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
        LogUtils.getConfig().setGlobalTag(TAG);
    }

    private void initEasyHttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        EasyConfig.with(okHttpClient)
                .setLogEnabled(BuildConfig.DEBUG)
                .setServer(AppConfig.APP_HOST)
                // 设置请求处理策略（必须设置）
                .setHandler(new RequestHandler())
                // 设置请求重试次数
                .setRetryCount(1)
                // 添加全局请求参数
                //.addParam("token", "6666666")
                // 添加全局请求头
                //.addHeader("time", "20191030")
                // 启用配置
                .into();
    }

    /**
     * 应监管要求，延迟初始化配置
     */
    public static void uengInit() {
        UMConfigure.preInit(myApplication, UMENGG_APP_ID, "android");
        UMConfigure.init(myApplication, UMENGG_APP_ID, "android", UMConfigure.DEVICE_TYPE_PHONE, "");
    }
}
