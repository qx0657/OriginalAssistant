package fun.qianxiao.originalassistant.utils;

import android.os.Build;

import com.blankj.utilcode.util.SPUtils;

import fun.qianxiao.originalassistant.bean.PostInfo;

/**
 * PostContentFormatUtils
 *
 * @Author QianXiao
 * @Date 2023/3/12
 */
public class PostContentFormatUtils {
    public static final String KEY_FIELD_NAME_APP_NAME = "KEY_FIELD_NAME_APP_NAME";
    public static final String KEY_FIELD_NAME_APP_LANGUAGE = "KEY_FIELD_NAME_APP_LANGUAGE";
    public static final String KEY_FIELD_NAME_APP_SIZE = "KEY_FIELD_NAME_APP_SIZE";
    public static final String KEY_FIELD_NAME_APP_VERSION = "KEY_FIELD_NAME_APP_VERSION";
    public static final String KEY_FIELD_NAME_APP_PACKAGE_NAME = "KEY_FIELD_NAME_APP_PACKAGE_NAME";
    public static final String KEY_FIELD_NAME_APP_VERSION_CODE = "KEY_FIELD_NAME_APP_VERSION_CODE";
    public static final String KEY_FIELD_NAME_APP_SYSTEM_VERSION = "KEY_FIELD_NAME_APP_SYSTEM_VERSION";
    public static final String KEY_FIELD_NAME_APP_SPECIAL_INSTRUCTIONS = "KEY_FIELD_NAME_APP_SPECIAL_INSTRUCTIONS";
    public static final String KEY_FIELD_NAME_APP_INTRODUCTION = "KEY_FIELD_NAME_APP_INTRODUCTION";
    public static final String KEY_FIELD_NAME_APP_DOWNLOAD_URL = "KEY_FIELD_NAME_APP_DOWNLOAD_URL";

    public static final String FIELD_NAME_APP_NAME = "【游戏名称】";
    public static final String FIELD_NAME_APP_LANGUAGE = "【游戏语言】";
    public static final String FIELD_NAME_APP_SIZE = "【游戏大小】";
    public static final String FIELD_NAME_APP_VERSION = "【游戏版本】";
    public static final String FIELD_NAME_APP_PACKAGE_NAME = "【游戏包名】";
    public static final String FIELD_NAME_APP_VERSION_CODE = "【开发代号】";
    public static final String FIELD_NAME_APP_SYSTEM_VERSION = "【系统版本】";
    public static final String FIELD_NAME_APP_SPECIAL_INSTRUCTIONS = "【特殊说明】";
    public static final String FIELD_NAME_APP_INTRODUCTION = "【游戏简介】";
    public static final String FIELD_NAME_APP_DOWNLOAD_URL = "【下载地址】";

    public static final String FIELD_SEPARATOR = "\n\n";

    public static CharSequence format(PostInfo postInfo) {
        String field_name_app_name = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_NAME, FIELD_NAME_APP_NAME);
        String field_name_app_language = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_LANGUAGE, FIELD_NAME_APP_LANGUAGE);
        String field_name_app_size = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_SIZE, FIELD_NAME_APP_SIZE);
        String field_name_app_version = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_VERSION, FIELD_NAME_APP_VERSION);
        String field_name_app_package_name = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_PACKAGE_NAME, FIELD_NAME_APP_PACKAGE_NAME);
        String field_name_app_version_code = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_VERSION_CODE, FIELD_NAME_APP_VERSION_CODE);
        String field_name_app_system_version = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_SYSTEM_VERSION, FIELD_NAME_APP_SYSTEM_VERSION);
        String field_name_app_special_instructions = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_SPECIAL_INSTRUCTIONS, FIELD_NAME_APP_SPECIAL_INSTRUCTIONS);
        String field_name_app_introduction = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_INTRODUCTION, FIELD_NAME_APP_INTRODUCTION);
        String field_name_app_download_url = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_DOWNLOAD_URL, FIELD_NAME_APP_DOWNLOAD_URL);

        return field_name_app_name + postInfo.getAppName() + FIELD_SEPARATOR +
                field_name_app_language + postInfo.getAppLanguage() + FIELD_SEPARATOR +
                field_name_app_size + postInfo.getAppSize() + FIELD_SEPARATOR +
                field_name_app_version + postInfo.getAppVersionName() + FIELD_SEPARATOR +
                field_name_app_package_name + postInfo.getAppPackageName() + FIELD_SEPARATOR +
                field_name_app_version_code + postInfo.getAppVersionCode() + FIELD_SEPARATOR +
                field_name_app_system_version + Build.VERSION.RELEASE + FIELD_SEPARATOR +
                field_name_app_special_instructions + postInfo.getAppSpecialInstructions() + FIELD_SEPARATOR +
                field_name_app_introduction + postInfo.getAppIntroduction() + FIELD_SEPARATOR +
                field_name_app_download_url + postInfo.getAppDownloadUrl();
    }
}
