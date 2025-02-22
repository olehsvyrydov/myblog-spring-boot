package org.javaprojects.myblogsite.dto;

import java.util.Collection;

/**
 * This class will be used for create/update 'post' data between server and client. Manages post data in feed page.
 */
public class FeedPostDto {
    private Long id;
    private String title;
    private String imageUrl;
    private String description;
    private Long commentsCount;
    private Long likesCount;
    private String tags;

    private FeedPostDto(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.imageUrl = builder.imageUrl;
        this.description = builder.description;
        this.commentsCount = builder.commentsCount;
        this.likesCount = builder.likesCount;
        this.tags = builder.tags;
    }

    public FeedPostDto() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public static class Builder {
        Long id;
        String title;
        String imageUrl;
        String description;
        Long commentsCount;
        Long likesCount;
        String tags;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setCommentsCount(Long commentsCount) {
            this.commentsCount = commentsCount;
            return this;
        }

        public Builder setLikesCount(Long likesCount) {
            this.likesCount = likesCount;
            return this;
        }

        public Builder setTags(String tags) {
            this.tags = tags;
            return this;
        }

        public Builder setTags(Collection<String> tags) {
            this.tags = String.join(", ", tags);
            return this;
        }

        public FeedPostDto create() {
            return new FeedPostDto(this);
        }
    }
}
