package fun.qianxiao.originalassistant.utils;

import android.os.Build;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.bean.PostInfo;
import fun.qianxiao.originalassistant.config.Constants;
import fun.qianxiao.originalassistant.config.SPConstants;

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

    public static final String FIELD_SEPARATOR = "\n";
    public static final String FIELD_SEPARATOR_DOUBLE = "\n\n";

    public static final String TITLE_FORMAT_DEFAULT = Utils.getApp().getString(R.string.post_title_format_default);

    public static CharSequence format(PostInfo postInfo) {
        String fieldNameAppName = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_NAME, FIELD_NAME_APP_NAME);
        String fieldNameAppLanguage = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_LANGUAGE, FIELD_NAME_APP_LANGUAGE);
        String fieldNameAppSize = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_SIZE, FIELD_NAME_APP_SIZE);
        String fieldNameAppVersion = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_VERSION, FIELD_NAME_APP_VERSION);
        String fieldNameAppPackageName = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_PACKAGE_NAME, FIELD_NAME_APP_PACKAGE_NAME);
        String fieldNameAppVersionCode = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_VERSION_CODE, FIELD_NAME_APP_VERSION_CODE);
        String fieldNameAppSystemVersion = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_SYSTEM_VERSION, FIELD_NAME_APP_SYSTEM_VERSION);
        String fieldNameAppSpecialInstructions = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_SPECIAL_INSTRUCTIONS, FIELD_NAME_APP_SPECIAL_INSTRUCTIONS);
        String fieldNameAppIntroduction = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_INTRODUCTION, FIELD_NAME_APP_INTRODUCTION);
        String fieldNameAppDownloadUrl = SPUtils.getInstance().getString(KEY_FIELD_NAME_APP_DOWNLOAD_URL, FIELD_NAME_APP_DOWNLOAD_URL);

        if (SPUtils.getInstance().getInt(SPConstants.KEY_APP_MODE, Constants.APP_MODE_GAME) == Constants.APP_MODE_SOFTWARE) {
            fieldNameAppName = fieldNameAppName.replace("游戏", "软件");
            fieldNameAppLanguage = fieldNameAppLanguage.replace("游戏", "软件");
            fieldNameAppSize = fieldNameAppSize.replace("游戏", "软件");
            fieldNameAppVersion = fieldNameAppVersion.replace("游戏", "软件");
            fieldNameAppPackageName = fieldNameAppPackageName.replace("游戏", "软件");
            fieldNameAppVersionCode = fieldNameAppVersionCode.replace("游戏", "软件");
            fieldNameAppSystemVersion = fieldNameAppSystemVersion.replace("游戏", "软件");
            fieldNameAppSpecialInstructions = fieldNameAppSpecialInstructions.replace("游戏", "软件");
            fieldNameAppIntroduction = fieldNameAppIntroduction.replace("游戏", "软件");
            fieldNameAppDownloadUrl = fieldNameAppDownloadUrl.replace("游戏", "软件");
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (SettingPreferences.getBoolean(R.string.p_key_switch_title)) {
            String teamName = SettingPreferences.getString(R.string.p_key_team_name, "");
            String titleFormat = SettingPreferences.getString(R.string.p_key_title_format, TITLE_FORMAT_DEFAULT);
            stringBuilder.append(String.format(titleFormat, teamName, postInfo.getAppName(), postInfo.getAppVersionName())).append(FIELD_SEPARATOR);
        }
        String postPrefix = SettingPreferences.getString(R.string.p_key_post_prefix);
        if (!TextUtils.isEmpty(postPrefix)) {
            stringBuilder.append(postPrefix).append(FIELD_SEPARATOR);
        }
        stringBuilder.append(fieldNameAppName).append(postInfo.getAppName()).append(FIELD_SEPARATOR_DOUBLE);
        stringBuilder.append(fieldNameAppLanguage).append(postInfo.getAppLanguage()).append(FIELD_SEPARATOR_DOUBLE);
        stringBuilder.append(fieldNameAppSize).append(postInfo.getAppSize()).append(FIELD_SEPARATOR_DOUBLE);
        stringBuilder.append(fieldNameAppVersion).append(postInfo.getAppVersionName()).append(FIELD_SEPARATOR_DOUBLE);
        stringBuilder.append(fieldNameAppPackageName).append(postInfo.getAppPackageName()).append(FIELD_SEPARATOR_DOUBLE);
        stringBuilder.append(fieldNameAppVersionCode).append(postInfo.getAppVersionCode()).append(FIELD_SEPARATOR_DOUBLE);
        stringBuilder.append(fieldNameAppSystemVersion).append(Build.VERSION.RELEASE).append(FIELD_SEPARATOR_DOUBLE);
        stringBuilder.append(fieldNameAppSpecialInstructions).append(postInfo.getAppSpecialInstructions()).append(FIELD_SEPARATOR_DOUBLE);
        stringBuilder.append(fieldNameAppIntroduction).append(postInfo.getAppIntroduction()).append(FIELD_SEPARATOR_DOUBLE);
        stringBuilder.append(fieldNameAppDownloadUrl).append(postInfo.getAppDownloadUrl());
        ;
        String postSuffix = SettingPreferences.getString(R.string.p_key_post_suffix);
        if (!TextUtils.isEmpty(postSuffix)) {
            stringBuilder.append(FIELD_SEPARATOR).append(postSuffix);
        }
        return stringBuilder.toString();
    }
}
