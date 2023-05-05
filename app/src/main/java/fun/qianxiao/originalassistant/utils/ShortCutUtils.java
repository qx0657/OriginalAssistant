package fun.qianxiao.originalassistant.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.originalassistant.R;

/**
 * ShortCutUtils
 *
 * @Author QianXiao
 * @Date 2023/5/5
 */
public class ShortCutUtils {
    public static void create(Context context, Intent intent, String label) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            intent.setAction(Intent.ACTION_VIEW);

            ShortcutInfoCompat info = new ShortcutInfoCompat.Builder(context, String.valueOf(System.currentTimeMillis()))
                    .setIcon(IconCompat.createWithResource(context, R.drawable.logo_huluxia))
                    .setShortLabel(label)
                    .setIntent(intent)
                    .build();
            PendingIntent shortcutCallbackIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                shortcutCallbackIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, MyReceiver.class), PendingIntent.FLAG_IMMUTABLE);
            } else {
                shortcutCallbackIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, MyReceiver.class), 0);
            }
            ShortcutManagerCompat.requestPinShortcut(context, info, shortcutCallbackIntent.getIntentSender());
        } else {
            ToastUtils.showShort("抱歉，您的设备不支持，请检查是否给予相关权限");
        }
    }

    public static class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i("MyReceiver onReceive: ");
        }
    }
}
