package fun.qianxiao.originalassistant.translate;

import com.blankj.utilcode.util.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fun.qianxiao.originalassistant.api.translate.GoogleTranslateApi;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

/**
 * GoogleTranslate
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class GoogleTranslate extends AbstractTranslate<GoogleTranslateApi> {
    @Override
    protected void response(JSONObject jsonObject, OnTranslateListener onTranslateListener) {
        LogUtils.i(jsonObject.toString());
    }

    @Override
    protected Observable<ResponseBody> request(String text) {
        String tk = getTk(text);
        return getApi().translate(text, tk);
    }

    private String getTk(String input) {
        int b = 406644;
        long b1 = 3293161072L;
        String jd = ".";
        String sb = "+-a^+6";
        String Zb = "+-3^+b+-f";
        List<Integer> e = new ArrayList<>();
        int f = 0;
        int g = 0;
        while (g < input.length()) {
            int m = input.charAt(g);
            if (m < 128) {
                e.add(m);
                f++;
            } else if (m < 2048) {
                e.add(m >> 6 | 192);
                f++;
                e.add(m & 63 | 128);
                f++;
            } else if ((m & 64512) == 55296 && g + 1 < input.length() && (input.charAt(g + 1) & 64512) == 56320) {
                m = 65536 + ((m & 1023) << 10) + (input.charAt(++g) & 1023);
                e.add(m >> 18 | 240);
                f++;
                e.add(m >> 12 & 63 | 128);
                f++;
                e.add(m >> 6 & 63 | 128);
                f++;
                e.add(m & 63 | 128);
                f++;
            } else {
                e.add(m >> 12 | 224);
                f++;
                e.add(m >> 6 & 63 | 128);
                f++;
                e.add(m & 63 | 128);
                f++;
            }
            g++;
        }
        long a = b;
        for (f = 0; f < e.size(); f++) {
            a += e.get(f);
            a = RL(a, sb);
        }
        a = RL(a, Zb);
        a ^= b1;
        if (a < 0) {
            a = (a & 2147483647) + 2147483648L;
        }
        a %= 1000000;
        return a + jd + (a ^ b1);
    }

    private long RL(long a, String b) {
        char t = 'a';
        char Yb = '+';
        for (int c = 0; c < b.length() - 2; c += 3) {
            long d = b.charAt(c + 2);
            d = d >= t ? d - 87 : Character.getNumericValue((char) d);
            d = b.charAt(c + 1) == Yb ? a >>> d : a << d;
            a = b.charAt(c) == Yb ? a + d & 4294967295L : a ^ d;
        }
        return a;
    }
}
