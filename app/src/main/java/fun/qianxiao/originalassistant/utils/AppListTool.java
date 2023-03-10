package fun.qianxiao.originalassistant.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import fun.qianxiao.originalassistant.bean.AppInfo;

/**
 * TODO
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class AppListTool {
    public static List<AppInfo> getAppList(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        List<AppInfo> appInfoList = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfoList) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppName(resolveInfo.loadLabel(packageManager));
            appInfo.setPackageName(resolveInfo.activityInfo.packageName);
            appInfo.setIcon(resolveInfo.loadIcon(packageManager));
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}
