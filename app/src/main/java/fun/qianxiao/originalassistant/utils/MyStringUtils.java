package fun.qianxiao.originalassistant.utils;

import android.text.TextUtils;

/**
 * MyStringUtils
 *
 * @Author QianXiao
 * @Date 2023/3/17
 */
public class MyStringUtils {

    /**
     * isNumeric
     *
     * @param s text
     * @return true or false
     */
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

    /**
     * join text array to string
     *
     * @param texts     text array
     * @param separator separator
     * @return result
     */
    public static String join(String[] texts, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texts.length; i++) {
            sb.append(texts[i]);
            if (i != texts.length - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }
}
