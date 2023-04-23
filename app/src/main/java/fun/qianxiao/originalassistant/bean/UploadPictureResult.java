package fun.qianxiao.originalassistant.bean;

/**
 * UploadPictureResult
 *
 * @Author QianXiao
 * @Date 2023/4/23
 */
public class UploadPictureResult {
    private String fid;
    private String url;

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UploadPictureResult{" +
                "url='" + url + '\'' +
                '}';
    }
}
