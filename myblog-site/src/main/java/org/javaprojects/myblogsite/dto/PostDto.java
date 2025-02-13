package org.javaprojects.myblogsite.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * this class is for data exchange between server/client. Manages post data in post page.
 */
public class PostDto {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private MultipartFile image;
    private String tags;
    private Long commentsCount;
    private List<CommentDto> comments;

    public PostDto() {
    }

    public PostDto(Long id, String title, String description, String content, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.imageUrl = imageUrl;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        Set<String> set = Set.of(tags.split("[,| ]\\s*"));
        setTags(set);
    }

    public void setTags(Collection<String> tags) {
        this.tags = String.join(", ", tags);
    }

    public Long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
