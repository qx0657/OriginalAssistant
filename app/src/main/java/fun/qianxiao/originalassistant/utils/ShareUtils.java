package fun.qianxiao.originalassistant.utils;

import android.content.Intent;
import android.net.Uri;

import com.blankj.utilcode.util.ActivityUtils;

/**
 * ShareUtils
 *
 * @Author QianXiao
 * @Date 2023/5/19
 */
public class ShareUtils {

    /**
     * shareImage
     *
     * @param file file
     */
    public static void shareImage(Uri file) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, file);
        shareIntent.setType("image/*");
        shareIntent = Intent.createChooser(shareIntent, "分享");
        ActivityUtils.startActivity(shareIntent);
    }
}
