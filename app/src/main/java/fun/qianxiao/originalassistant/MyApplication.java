package fun.qianxiao.originalassistant;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.umeng.commonsdk.UMConfigure;

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

        //logger
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

        // 友盟
        UMConfigure.setLogEnabled(true);
    }

    /**
     * 应监管要求，延迟初始化配置
     */
    public static void uengInit() {
        UMConfigure.preInit(myApplication, UMENGG_APP_ID, "android");
        UMConfigure.init(myApplication, UMENGG_APP_ID, "android", UMConfigure.DEVICE_TYPE_PHONE, "");
    }
}
