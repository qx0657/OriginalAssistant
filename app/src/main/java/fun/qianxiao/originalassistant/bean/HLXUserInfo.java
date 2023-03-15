package fun.qianxiao.originalassistant.bean;

/**
 * HLXUserInfo
 *
 * @Author QianXiao
 * @Date 2023/3/15
 */
public class HLXUserInfo {
    /**
     * ID
     */
    private long userId;
    /**
     * 昵称
     */
    private String nick;
    /**
     * 头像链接
     */
    private String avatarUrl;
    /**
     * 发帖数
     */
    private int postCount;
    /**
     * 回帖数
     */
    private int commentCount;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
