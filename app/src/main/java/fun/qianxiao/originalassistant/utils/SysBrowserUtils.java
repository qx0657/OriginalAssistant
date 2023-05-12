package fun.qianxiao.originalassistant.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;

/**
 * SysBrowserUtils
 *
 * @Author QianXiao
 * @Date 2023/4/13
 */
public class SysBrowserUtils {
    public static final String VIA_BROWSER = "mark.via";
    public static final String VIA_BROWSER_ACTIVITY = "mark.via.Shell";

    /**
     * open url by sys browser
     *
     * @param context context
     * @param url     url
     */
    public static void open(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (AppUtils.isAppInstalled(VIA_BROWSER)) {
            ComponentName componentName = new ComponentName(VIA_BROWSER, VIA_BROWSER_ACTIVITY);
            intent.setComponent(componentName);
            context.startActivity(intent);
            return;
        }
        try {
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("url", url);
            String title = "选择浏览器";
            Intent intentChooser = Intent.createChooser(intent, title);
            context.startActivity(intentChooser);
        } catch (Exception e) {
            ToastUtils.showShort(e.getMessage());
        }
    }
}
