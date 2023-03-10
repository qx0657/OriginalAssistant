package fun.qianxiao.originalassistant.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fun.qianxiao.originalassistant.bean.AppInfo;

/**
 * AppListTool
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class AppListTool {
    public static List<AppInfo> getAppList(Context context) throws PackageManager.NameNotFoundException {
        List<AppInfo> appInfoList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        int type = 2;
        if (type == 1) {
            List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
            for (PackageInfo packageInfo : packageInfoList) {
                AppInfo appInfo = new AppInfo();
                appInfo.setAppName(packageInfo.applicationInfo.loadLabel(packageManager));
                appInfo.setPackageName(packageInfo.packageName);
                appInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
                appInfo.setLastUpdateTime(packageInfo.lastUpdateTime);
                appInfoList.add(appInfo);
            }
        } else if (type == 2) {
            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
            for (ResolveInfo resolveInfo : resolveInfoList) {
                AppInfo appInfo = new AppInfo();
                PackageInfo packageInfo = packageManager.getPackageInfo(resolveInfo.activityInfo.packageName, PackageManager.GET_ATTRIBUTIONS);
                appInfo.setAppName(resolveInfo.loadLabel(packageManager));
                appInfo.setPackageName(resolveInfo.activityInfo.packageName);
                appInfo.setIcon(resolveInfo.loadIcon(packageManager));
                appInfo.setLastUpdateTime(packageInfo.lastUpdateTime);
                appInfoList.add(appInfo);
            }
        }
        Collections.sort(appInfoList);
        return appInfoList;
    }
}
