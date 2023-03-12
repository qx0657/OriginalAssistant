package fun.qianxiao.originalassistant.bean;

/**
 * PostInfo
 *
 * @Author QianXiao
 * @Date 2023/3/12
 */
public class PostInfo {
    private CharSequence appPackageName;
    private CharSequence appName;
    private CharSequence appSize;
    private CharSequence appVersionName;
    private CharSequence appVersionCode;
    private AppLanguage appLanguage;
    private CharSequence appSpecialInstructions;
    private CharSequence appIntroduction;
    private CharSequence appDownloadUrl;

    public enum AppLanguage {
        /**
         * 中文游戏
         */
        CHINESE("中文"),
        /**
         * 英文游戏
         */
        ENGLISH("英文"),
        /**
         * 其他
         */
        OTHER("其他");

        private String value;

        AppLanguage(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public CharSequence getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(CharSequence appPackageName) {
        this.appPackageName = appPackageName;
    }

    public CharSequence getAppName() {
        return appName;
    }

    public void setAppName(CharSequence appName) {
        this.appName = appName;
    }

    public CharSequence getAppSize() {
        return appSize;
    }

    public void setAppSize(CharSequence appSize) {
        this.appSize = appSize;
    }

    public CharSequence getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(CharSequence appVersionName) {
        this.appVersionName = appVersionName;
    }

    public CharSequence getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(CharSequence appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public AppLanguage getAppLanguage() {
        return appLanguage;
    }

    public void setAppLanguage(AppLanguage appLanguage) {
        this.appLanguage = appLanguage;
    }

    public CharSequence getAppSpecialInstructions() {
        return appSpecialInstructions;
    }

    public void setAppSpecialInstructions(CharSequence appSpecialInstructions) {
        this.appSpecialInstructions = appSpecialInstructions;
    }

    public CharSequence getAppIntroduction() {
        return appIntroduction;
    }

    public void setAppIntroduction(CharSequence appIntroduction) {
        this.appIntroduction = appIntroduction;
    }

    public CharSequence getAppDownloadUrl() {
        return appDownloadUrl;
    }

    public void setAppDownloadUrl(CharSequence appDownloadUrl) {
        this.appDownloadUrl = appDownloadUrl;
    }
}
