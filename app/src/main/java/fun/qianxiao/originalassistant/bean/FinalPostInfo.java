package fun.qianxiao.originalassistant.bean;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import fun.qianxiao.originalassistant.adapter.BooleanTypeAdapter;

/**
 * FinalPostInfo
 *
 * @Author QianXiao
 * @Date 2023/5/14
 */
public class FinalPostInfo {
    @SerializedName("postID")
    private long postId;
    private String title;
    private String detail;
    private String[] images;
    private int score;
    private String scoreTxt;
    private int hit;
    private int commentCount;
    private int notice;
    private int weight;
    @JsonAdapter(BooleanTypeAdapter.class)
    private boolean isGood;
    private long createTime;
    private long activeTime;
    @SerializedName("category")
    private CategoryInfo categoryInfo = new CategoryInfo();

    public static class CategoryInfo {
        @SerializedName("categoryID")
        private int categoryId;

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getScoreTxt() {
        return scoreTxt;
    }

    public void setScoreTxt(String scoreTxt) {
        this.scoreTxt = scoreTxt;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getNotice() {
        return notice;
    }

    public void setNotice(int notice) {
        this.notice = notice;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }

    public CategoryInfo getCategoryInfo() {
        return categoryInfo;
    }

    public void setCategoryInfo(CategoryInfo categoryInfo) {
        this.categoryInfo = categoryInfo;
    }

    @Override
    public String toString() {
        return "FinalPostInfo{" +
                "postId=" + postId +
                ", title='" + title + '\'' +
                '}';
    }
}
