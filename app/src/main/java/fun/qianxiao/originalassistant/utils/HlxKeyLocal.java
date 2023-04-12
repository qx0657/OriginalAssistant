package fun.qianxiao.originalassistant.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.SPUtils;

import fun.qianxiao.originalassistant.config.SPConstants;

/**
 * HlxKeyLocal
 *
 * @Author QianXiao
 * @Date 2023/4/12
 */
public class HlxKeyLocal {
    private static final String CRYPT_KEY_STR = "ea18e3e2da386cb42db8cb8634121c13";
    private static final byte[] CRYPT_KEY = ConvertUtils.hexString2Bytes(CRYPT_KEY_STR);
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static String read() {
        String key = SPUtils.getInstance().getString(SPConstants.KEY_HLX_KEY);
        if (TextUtils.isEmpty(key)) {
            return key;
        }
        byte[] keyP = EncryptUtils.decryptHexStringAES(
                key,
                CRYPT_KEY,
                TRANSFORMATION,
                null
        );
        return ConvertUtils.bytes2String(keyP);
    }

    public static void write(String key) {
        String keyC = EncryptUtils.encryptAES2HexString(
                ConvertUtils.string2Bytes(key),
                CRYPT_KEY,
                TRANSFORMATION,
                null
        );
        SPUtils.getInstance().put(SPConstants.KEY_HLX_KEY, keyC);
    }
}
