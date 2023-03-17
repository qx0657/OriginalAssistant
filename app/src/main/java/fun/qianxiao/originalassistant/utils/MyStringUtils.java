package fun.qianxiao.originalassistant.utils;

import android.text.TextUtils;

/**
 * MyStringUtils
 *
 * @Author QianXiao
 * @Date 2023/3/17
 */
public class MyStringUtils {

    public static boolean isNumeric(final CharSequence s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
