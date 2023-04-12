package fun.qianxiao.originalassistant.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * SysBrowserUtils
 *
 * @Author QianXiao
 * @Date 2023/4/13
 */
public class SysBrowserUtils {
    /**
     * open url by sys browser
     *
     * @param context context
     * @param url     url
     */
    public static void open(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("url", url);
        String title = "选择浏览器";
        Intent intentChooser = Intent.createChooser(intent, title);
        context.startActivity(intentChooser);
    }
}
