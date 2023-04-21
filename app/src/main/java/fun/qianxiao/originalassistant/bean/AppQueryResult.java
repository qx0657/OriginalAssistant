package fun.qianxiao.originalassistant.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AppQueryResult
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class AppQueryResult {
    private String packageName;
    private String appName;
    private String appIntroduction;
    private List<String> appPictures;

    public AppQueryResult() {
    }

    public AppQueryResult(String packageName, String appName) {
        this.packageName = packageName;
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppIntroduction() {
        return appIntroduction;
    }

    public void setAppIntroduction(String appIntroduction) {
        this.appIntroduction = appIntroduction;
    }

    public List<String> getAppPictures() {
        return appPictures;
    }

    /**
     * setAppPictures
     *
     * @param appPictures It's best not to use Arrays.asList(T[])
     */
    public void setAppPictures(List<String> appPictures) {
        this.appPictures = appPictures;
    }


    public void setAppPictures(String[] strings) {
        appPictures = new ArrayList<>();
        appPictures.addAll(Arrays.asList(strings));
    }
}
