package fun.qianxiao.originalassistant.config;

import com.blankj.utilcode.util.PathUtils;

import java.io.File;

/**
 * AppConfig
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class AppConfig {
    /**
     * App英文名
     */
    public static final String APP_NAME_EN = "OriginalAssistant";
    /**
     * 域名
     */
    public static final String APP_HOST = "http://qianxiao.fun";
    /**
     * 检查更新配置
     */
    public static final String CHECKUPDATE_URL = "http://qianxiao.fun/app/original_assistant/update_config.php";
    /**
     * 隐私政策链接
     */
    public static final String PRIVACY_POLICY_URL = "http://qianxiao.fun/app/original_assistant/privacy_policy.html";
    /**
     * 帮助中心链接
     */
    public static final String HELP_URL = "http://qianxiao.fun/app/original_assistant/help.html";
    /**
     * 项目Github
     */
    public static final String PROJECT_GITHUB_URL = "https://github.com/qx0657/OriginalAssistant";
    /**
     * App外部目录
     */
    public static final String APP_EXTERNAL_DIR = PathUtils.getExternalStoragePath() + File.separator + APP_NAME_EN;
    /**
     * 默认Apk导出目录
     */
    public static final String DEFAULT_APK_EXPORT_DIR = APP_EXTERNAL_DIR + File.separator + "apks";
    /**
     * 3楼App包名列表
     */
    public static final String[] HULUXIA_APP_PACKAGE_NAME = new String[]{
            "com.huati",
            "com.huluxia.gametools"
    };
    /**
     * 3楼App主Activity名，用于免启动页跳转
     */
    public static final String[] HULUXIA_APP_HOME_ACTIVITY_NAME = new String[]{
            "com.huluxia.ui.home.HomeActivity",
            "com.huluxia.ui.home.ToolHomeActivity"
    };
}
