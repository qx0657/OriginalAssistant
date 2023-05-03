package fun.qianxiao.originalassistant.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import fun.qianxiao.originalassistant.config.AppConfig;

/**
 * HLXUtils
 *
 * @Author QianXiao
 * @Date 2023/4/20
 */
public class HLXUtils {
    private static final String SECRET = "my_sign@huluxia.com";
    private static final Random RANDOM = new SecureRandom();
    private static final String SCHEME_FLOOR = "hlx.floor";
    private static final String SCHEME_TOOL = "hlx";

    public static String getNonceStr() {
        char[] nonceChars = new char[32];
        for (int index = 0; index < nonceChars.length; index++) {
            nonceChars[index] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(RANDOM.nextInt("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length()));
        }
        return new String(nonceChars);
    }

    /**
     * sign
     * for upload image
     *
     * @param data data map
     * @return sign result
     */
    public static String sign(Map<String, String> data) {
        Set<String> keySet = data.keySet();
        int arraySize = keySet.size();
        String[] keyArray = (String[]) keySet.toArray(new String[arraySize]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (!"sign".equals(k) && data.get(k) != null && data.get(k).trim().length() > 0) {
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
            }
        }
        sb.append("secret=").append(SECRET);
        return EncryptUtils.encryptMD5ToString(sb.toString()).toUpperCase(Locale.ROOT);
    }

    public static String getDeviceCode() {
        return "[d]" +
                UUID.randomUUID().toString();
    }

    /**
     * Jump to the homepage of hlx app.
     * Starting with a non package name, use ComponentName to jump to HomeActivity and accelerate the jump speed
     * HomeActivity currently has an external attribute of true (android: exported='true'), so it can be used. Otherwise, there is no permission
     */
    public static void gotoHLX() {
        String appPackageNameInstalled = null;
        for (String s : AppConfig.HULUXIA_APP_PACKAGE_NAME) {
            if (AppUtils.isAppInstalled(s)) {
                appPackageNameInstalled = s;
                break;
            }
        }
        if (!TextUtils.isEmpty(appPackageNameInstalled)) {
            try {
                Intent intent = new Intent();
                ComponentName componentName = null;
                assert appPackageNameInstalled != null;
                if (appPackageNameInstalled.equals(AppConfig.HULUXIA_APP_PACKAGE_NAME[0])) {
                    componentName = new ComponentName(AppConfig.HULUXIA_APP_PACKAGE_NAME[0], AppConfig.HULUXIA_APP_HOME_ACTIVITY_NAME[0]);
                } else if (appPackageNameInstalled.equals(AppConfig.HULUXIA_APP_PACKAGE_NAME[1])) {
                    componentName = new ComponentName(AppConfig.HULUXIA_APP_PACKAGE_NAME[1], AppConfig.HULUXIA_APP_HOME_ACTIVITY_NAME[1]);
                }
                intent.setComponent(componentName);
                intent.putExtra("tab_index", 3);
                ActivityUtils.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showShort("跳转失败");
            }
        } else {
            ToastUtils.showShort("没有安装3楼");
        }
    }

    /**
     * Get hlx app scheme
     *
     * @return hlx app scheme
     */
    public static String getHlxScheme() {
        String appPackageNameInstalled = null;
        for (String s : AppConfig.HULUXIA_APP_PACKAGE_NAME) {
            if (AppUtils.isAppInstalled(s)) {
                appPackageNameInstalled = s;
                break;
            }
        }
        if (!TextUtils.isEmpty(appPackageNameInstalled)) {
            if (appPackageNameInstalled.equals(AppConfig.HULUXIA_APP_PACKAGE_NAME[0])) {
                return SCHEME_FLOOR;
            }
        }
        return SCHEME_TOOL;
    }
}
