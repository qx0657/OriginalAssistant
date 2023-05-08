package fun.qianxiao.originalassistant.bean;

/**
 * HLXPicBedUploadHistory
 *
 * @Author QianXiao
 * @Date 2023/5/8
 */
public class HLXPicBedUploadHistory {
    private String filePath;
    private UploadPictureResult uploadPictureResult;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public UploadPictureResult getUploadPictureResult() {
        return uploadPictureResult;
    }

    public void setUploadPictureResult(UploadPictureResult uploadPictureResult) {
        this.uploadPictureResult = uploadPictureResult;
    }
}
