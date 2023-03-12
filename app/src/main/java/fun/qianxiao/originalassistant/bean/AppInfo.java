package fun.qianxiao.originalassistant.bean;

import android.graphics.drawable.Drawable;

/**
 * Minimized AppInfo
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class AppInfo implements Comparable<AppInfo> {
    /**
     * For display
     */
    private String packageName;
    /**
     * For display
     */
    private CharSequence appName;
    /**
     * For display
     */
    private Drawable icon;
    /**
     * Unnecessary
     */
    private String versionName;
    /**
     * Unnecessary
     */
    private int versionCode;
    /**
     * For sort
     */
    private long lastUpdateTime;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public CharSequence getAppName() {
        return appName;
    }

    public void setAppName(CharSequence appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", appName=" + appName +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }

    @Override
    public int compareTo(AppInfo o) {
        if (o.lastUpdateTime > lastUpdateTime) {
            return 1;
        } else if (o.lastUpdateTime < lastUpdateTime) {
            return -1;
        }
        return 0;
    }
}
