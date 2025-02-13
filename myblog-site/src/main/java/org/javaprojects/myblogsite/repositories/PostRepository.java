package org.javaprojects.myblogsite.repositories;

import org.javaprojects.myblogsite.dto.CommentDto;
import org.javaprojects.myblogsite.dto.FeedPostDto;
import org.javaprojects.myblogsite.models.Post;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostRepository {
    Optional<Post> findPostById(long id);

    List<FeedPostDto> findAllPosts(int from, int number);

    long save(Post post);

    void update(Post post);

    void delete(long postId);

    void addComment(CommentDto comment);

    void updateComment(CommentDto comment);

    void deleteComment(long commentId);

    List<CommentDto> findCommentsByPostId(long id);

    long getCommentsNumberByPost(long postId);

    void insertTags(Set<String> tags, long postId);

    void deleteTagsForPostId(long postId);

    Set<String> getAllTags();

    Set<String> findTagsByPostId(long postId);

    List<FeedPostDto> findPostsByTag(String tagName, int from, int number);

    void addLike(long postId);

    long getLikesNumberByPostId(long postId);

    long getTotalPostsCount();
}
