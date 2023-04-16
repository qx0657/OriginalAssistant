package fun.qianxiao.originalassistant.utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.blankj.utilcode.util.Utils;

import java.util.Map;

/**
 * TODO
 *
 * @Author QianXiao
 * @Date 2023/4/15
 */
public enum SettingPreferences {
    /**
     * Instance
     */
    SettingPreferences;

    private static SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Utils.getApp());

    public static boolean getBoolean(int key) {
        return getBoolean(Utils.getApp().getString(key));
    }

    public static boolean getBoolean(int key, boolean defValue) {
        return getBoolean(Utils.getApp().getString(key), defValue);
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    public static String getString(int key) {
        return getString(Utils.getApp().getString(key));
    }

    public static String getString(int key, String defValue) {
        return getString(Utils.getApp().getString(key), defValue);
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    public static Object get(String key) {
        Map<String, ?> map = preferences.getAll();

        for (String s : map.keySet()) {
            if (s.equals(key)) {
                return map.get(s);
            }
        }
        return null;
    }
}
