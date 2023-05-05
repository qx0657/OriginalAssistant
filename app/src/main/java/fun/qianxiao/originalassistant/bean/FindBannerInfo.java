package fun.qianxiao.originalassistant.bean;

import com.stx.xhb.androidx.entity.BaseBannerInfo;

/**
 * FindBannerInfo
 * hlx activity info
 *
 * @Author QianXiao
 * @Date 2023/5/5
 */
public class FindBannerInfo implements BaseBannerInfo {
    private long id;
    private String imageUrl;
    private String title;
    private MODE mode = MODE.URL;
    /**
     * When {@link FindBannerInfo#mode} is {@link MODE#URL}
     * can use activity id jump
     */
    private String url;
    /**
     * When {@link FindBannerInfo#mode} is {@link MODE#POST}
     */
    private long postId;

    public FindBannerInfo(long id, String imageUrl, String title) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public enum MODE {
        /**
         * jump_mode is a post id, in this mode, can jump to post detail by post id.
         */
        POST,
        /**
         * jump_mode is a url, in this mode, can jump directly  by using activity id .
         */
        URL
    }

    public long getId() {
        return id;
    }

    public FindBannerInfo setId(long id) {
        this.id = id;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public FindBannerInfo setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public FindBannerInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    public MODE getMode() {
        return mode;
    }

    public FindBannerInfo setMode(MODE mode) {
        this.mode = mode;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public FindBannerInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public long getPostId() {
        return postId;
    }

    public FindBannerInfo setPostId(long postId) {
        this.postId = postId;
        return this;
    }

    @Override
    public String getXBannerUrl() {
        return imageUrl;
    }

    @Override
    public String getXBannerTitle() {
        return title;
    }
}
