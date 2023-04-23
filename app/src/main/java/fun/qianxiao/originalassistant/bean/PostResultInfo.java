package fun.qianxiao.originalassistant.bean;

/**
 * PostResultInfo
 *
 * @Author QianXiao
 * @Date 2023/4/23
 */
public class PostResultInfo {
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_IN_REVIEW = 201;
    private long postId;
    private String msg;
    private int code;

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
