package fun.qianxiao.originalassistant.activity.opensourcelicense.bean;

public class OpenSourceLicense {
    private String name;
    private String anthor;
    private String describe;
    private String license;
    private String url;

    public OpenSourceLicense() {
    }

    public OpenSourceLicense(String name, String anthor, String describe, String license, String url) {
        this.name = name;
        this.anthor = anthor;
        this.describe = describe;
        this.license = license;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnthor() {
        return anthor;
    }

    public void setAnthor(String anthor) {
        this.anthor = anthor;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
