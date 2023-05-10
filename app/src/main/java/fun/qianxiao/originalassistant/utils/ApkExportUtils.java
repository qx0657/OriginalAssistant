package fun.qianxiao.originalassistant.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import fun.qianxiao.originalassistant.manager.PermissionManager;

/**
 * ApkExportUtils
 *
 * @Author QianXiao
 * @Date 2023/5/9
 */
public class ApkExportUtils {

    public static boolean checkPermissionAndExportApk(Context context, String packageName, String outFileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                return exportApk(packageName, outFileName);
            } else {
                ToastUtils.showShort("请给予" + AppUtils.getAppName() + "所有文件权限");
                PermissionManager.getInstance().requestManageExternalStoragePermission(context);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(Utils.getApp(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(Utils.getApp(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return exportApk(packageName, outFileName);
            } else {
                ToastUtils.showShort("请给予" + AppUtils.getAppName() + "文件读写权限");
                PermissionManager.getInstance().requestReadWritePermission();
            }
        }
        return false;
    }

    public static boolean exportApk(String packageName, String outFileName) {
        PackageManager packageManager = Utils.getApp().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return false;
            }
            String appPath = packageInfo.applicationInfo.sourceDir;
            String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            LogUtils.i(appPath, outFileName);
            if (FileUtils.copy(appPath, outFileName)) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
