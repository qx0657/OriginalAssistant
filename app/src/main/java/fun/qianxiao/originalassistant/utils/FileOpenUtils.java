package fun.qianxiao.originalassistant.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.UriUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import fun.qianxiao.originalassistant.BuildConfig;

/**
 * FileOpenUtils
 *
 * @Author QianXiao
 * @Date 2023/5/10
 */
public class FileOpenUtils {

    public static void openWithMt(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(UriUtils.file2Uri(file));
        ComponentName componentName = new ComponentName("bin.mt.plus", "bin.mt.plus.OpenFileActivity");
        intent.setComponent(componentName);
        ActivityUtils.startActivity(intent);
    }

    private static boolean isInstallMt() {
        return AppUtils.isAppInstalled("bin.mt.plus");
    }

    public static void openFile(Context context, File file) {
        if (isInstallMt()) {
            openWithMt(file);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // intent.addCategory(Intent.CATEGORY_DEFAULT);
        Uri uriForFile;
        if (Build.VERSION.SDK_INT > 23) {
            uriForFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".utilcode.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        } else {
            uriForFile = Uri.fromFile(file);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uriForFile, getMimeTypeFromFile(file));
        context.startActivity(intent);
    }

    private static String getMimeTypeFromFile(File file) {
        String type = "*/*";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex > 0) {
            String end = fName.substring(dotIndex, fName.length()).toLowerCase(Locale.getDefault());
            HashMap<String, String> map = MyMimeMap.getMimeMap();
            if (!TextUtils.isEmpty(end) && map.keySet().contains(end)) {
                type = map.get(end);
            }
        }
        return type;
    }

    static class MyMimeMap {
        private static final HashMap<String, String> mapSimple = new HashMap<>();

        public static HashMap<String, String> getMimeMap() {
            if (mapSimple.size() == 0) {
                mapSimple.put(".3gp", "video/3gpp");
                // mapSimple.put(".apk", "application/vnd.android.package-archive");
                mapSimple.put(".asf", "video/x-ms-asf");
                mapSimple.put(".avi", "video/x-msvideo");
                mapSimple.put(".bin", "application/octet-stream");
                mapSimple.put(".bmp", "image/bmp");
                mapSimple.put(".c", "text/plain");
                mapSimple.put(".chm", "application/x-chm");
                mapSimple.put(".class", "application/octet-stream");
                mapSimple.put(".conf", "text/plain");
                mapSimple.put(".cpp", "text/plain");
                mapSimple.put(".doc", "application/msword");
                mapSimple.put(".docx", "application/msword");
                mapSimple.put(".exe", "application/octet-stream");
                mapSimple.put(".gif", "image/gif");
                mapSimple.put(".gtar", "application/x-gtar");
                mapSimple.put(".gz", "application/x-gzip");
                mapSimple.put(".h", "text/plain");
                mapSimple.put(".htm", "text/html");
                mapSimple.put(".html", "text/html");
                mapSimple.put(".jar", "application/java-archive");
                mapSimple.put(".java", "text/plain");
                mapSimple.put(".jpeg", "image/jpeg");
                mapSimple.put(".jpg", "image/jpeg");
                mapSimple.put(".js", "application/x-javascript");
                mapSimple.put(".log", "text/plain");
                mapSimple.put(".m3u", "audio/x-mpegurl");
                mapSimple.put(".m4a", "audio/mp4a-latm");
                mapSimple.put(".m4b", "audio/mp4a-latm");
                mapSimple.put(".m4p", "audio/mp4a-latm");
                mapSimple.put(".m4u", "video/vnd.mpegurl");
                mapSimple.put(".m4v", "video/x-m4v");
                mapSimple.put(".mov", "video/quicktime");
                mapSimple.put(".mp2", "audio/x-mpeg");
                mapSimple.put(".mp3", "audio/x-mpeg");
                mapSimple.put(".mp4", "video/mp4");
                mapSimple.put(".mpc", "application/vnd.mpohun.certificate");
                mapSimple.put(".mpe", "video/mpeg");
                mapSimple.put(".mpeg", "video/mpeg");
                mapSimple.put(".mpg", "video/mpeg");
                mapSimple.put(".mpg4", "video/mp4");
                mapSimple.put(".mpga", "audio/mpeg");
                mapSimple.put(".msg", "application/vnd.ms-outlook");
                mapSimple.put(".ogg", "audio/ogg");
                mapSimple.put(".pdf", "application/pdf");
                mapSimple.put(".png", "image/png");
                mapSimple.put(".pps", "application/vnd.ms-powerpoint");
                mapSimple.put(".ppt", "application/vnd.ms-powerpoint");
                mapSimple.put(".pptx", "application/vnd.ms-powerpoint");
                mapSimple.put(".prop", "text/plain");
                mapSimple.put(".rar", "application/x-rar-compressed");
                mapSimple.put(".rc", "text/plain");
                mapSimple.put(".rmvb", "audio/x-pn-realaudio");
                mapSimple.put(".rtf", "application/rtf");
                mapSimple.put(".sh", "text/plain");
                mapSimple.put(".tar", "application/x-tar");
                mapSimple.put(".tgz", "application/x-compressed");
                mapSimple.put(".txt", "text/plain");
                mapSimple.put(".wav", "audio/x-wav");
                mapSimple.put(".wma", "audio/x-ms-wma");
                mapSimple.put(".wmv", "audio/x-ms-wmv");
                mapSimple.put(".wps", "application/vnd.ms-works");
                mapSimple.put(".xml", "text/plain");
                mapSimple.put(".xls", "application/vnd.ms-excel");
                mapSimple.put(".xlsx", "application/vnd.ms-excel");
                mapSimple.put(".z", "application/x-compress");
                mapSimple.put(".zip", "application/zip");
                mapSimple.put("", "*/*");
            }
            return mapSimple;
        }
    }
}
