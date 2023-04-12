package fun.qianxiao.originalassistant.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * SysMailUtils
 *
 * @Author QianXiao
 * @Date 2023/4/13
 */
public class SysMailUtils {
    /**
     * send email by sys default app
     *
     * @param context context
     * @param email   email
     * @param subject subject
     */
    public static void send(Context context, String email, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
