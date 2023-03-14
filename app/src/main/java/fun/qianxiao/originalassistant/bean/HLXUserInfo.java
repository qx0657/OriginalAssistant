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
}
