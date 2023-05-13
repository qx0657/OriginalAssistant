package fun.qianxiao.originalassistant.utils;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.bean.AppInfo;

/**
 * AppListTool
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class AppListTool {
    public static List<AppInfo> getAppList() throws PackageManager.NameNotFoundException {
        long time0 = System.currentTimeMillis();
        List<AppInfo> appInfoList = new ArrayList<>();
        PackageManager packageManager = Utils.getApp().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        long time1 = System.currentTimeMillis();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        long time2 = System.currentTimeMillis();
        for (ResolveInfo resolveInfo : resolveInfoList) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppName(resolveInfo.loadLabel(packageManager));
            appInfo.setPackageName(resolveInfo.activityInfo.packageName);
            if (!SettingPreferences.getBoolean(R.string.p_key_switch_get_sys_app_list_with_no_icon)) {
                // time-consuming
                appInfo.setIcon(resolveInfo.loadIcon(packageManager));
            }
            PackageInfo packageInfo;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                packageInfo = packageManager.getPackageInfo(resolveInfo.activityInfo.packageName, PackageManager.GET_ATTRIBUTIONS);
            } else {
                packageInfo = packageManager.getPackageInfo(resolveInfo.activityInfo.packageName, 0);
            }
            appInfo.setVersionName(packageInfo.versionName);
            appInfo.setVersionCode(packageInfo.versionCode);
            appInfo.setLastUpdateTime(packageInfo.lastUpdateTime);
            appInfoList.add(appInfo);
        }
        long time3 = System.currentTimeMillis();
        Collections.sort(appInfoList);
        long time4 = System.currentTimeMillis();
        LogUtils.i("prepare use " + (time1 - time0) + "ms",
                "queryIntentActivities use " + (time2 - time1) + "ms",
                "resolveInfoList size " + resolveInfoList.size(),
                "for getPackageInfo use " + (time3 - time2) + "ms",
                "sort use " + (time4 - time3) + "ms");
        return appInfoList;
    }
}
