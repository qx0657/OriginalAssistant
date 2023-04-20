package fun.qianxiao.originalassistant.utils;

import com.blankj.utilcode.util.EncryptUtils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * HLXUtils
 *
 * @Author QianXiao
 * @Date 2023/4/20
 */
public class HLXUtils {
    private static final String SECRET = "my_sign@huluxia.com";
    private static final Random RANDOM = new SecureRandom();

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
}
