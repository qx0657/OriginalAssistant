package fun.qianxiao.originalassistant.bean;

/**
 * AnalysisResult
 *
 * @Author QianXiao
 * @Date 2023/4/18
 */
public class AnalysisResult {
    private String api;
    private AppQueryResult appQueryResult = new AppQueryResult();
    private String errorMsg;
    private boolean success;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public AppQueryResult getAppQueryResult() {
        return appQueryResult;
    }

    public void setAppQueryResult(AppQueryResult appQueryResult) {
        this.appQueryResult = appQueryResult;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
