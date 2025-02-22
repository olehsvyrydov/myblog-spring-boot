package org.javaprojects.myblogsite.repositories;

import org.javaprojects.myblogsite.dto.CommentDto;
import org.javaprojects.myblogsite.dto.FeedPostDto;
import org.javaprojects.myblogsite.models.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.jdbc.JdbcTestUtils.*;

@ActiveProfiles("test")
@JdbcTest
@Import(JdbcPostRepository.class)
@Transactional
class PostRepositoryTest {

    public static final String TEST_TITLE = "Test Title ";
    public static final String TEST_DESCRIPTION = "Short description ";
    public static final String TEST_IMAGE = "/images/default_image.jpg";
    public static final String TEST_CONTENT = "This is content for ";
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment env;

    @AfterEach
    void setUp() {
        deleteFromTables(jdbcTemplate, "posts");
        deleteFromTables(jdbcTemplate, "comments");
        deleteFromTables(jdbcTemplate, "tags");
        deleteFromTables(jdbcTemplate, "likes");
    }

    @Test
    void findPostById() {
        // Given
        Post expectedPost = getExpectedPost(1L);
        postRepository.save(expectedPost);
        // When
        Optional<Post> optionalPost = postRepository.findPostById(1L);
        // Then
        optionalPost.ifPresent(actualPost ->
            Assertions.assertAll(
                    () -> assertEquals(expectedPost.id(), actualPost.id()),
                    () -> assertEquals(expectedPost.title(), actualPost.title()),
                    () -> assertEquals(expectedPost.description(), actualPost.description()),
                    () -> assertEquals(expectedPost.content(), actualPost.content()),
                    () -> assertEquals(expectedPost.imageUrl(), actualPost.imageUrl()),
                    () -> assertEquals(expectedPost.createdAt().toEpochSecond(ZoneOffset.UTC),
                            actualPost.createdAt().toEpochSecond(ZoneOffset.UTC))
            )
        );
    }

    @Test
    void findAllPosts() throws InterruptedException {
        // Given
        List<Long> ids = new ArrayList<>(10);
        for (long i = 1; i <= 10; i++) {
            Post expectedPost = getExpectedPost(i);
            long postId = postRepository.save(expectedPost);
            ids.add(postId);
            IntStream.range(0, 20).forEach(k -> postRepository.addLike(postId));
            CommentDto comment = new CommentDto();
            comment.setPostId(postId);
            comment.setContent("This is comment content for post id = " + postId);
            comment.setCreatedAt(LocalDateTime.now());
            postRepository.addComment(comment);
            postRepository.insertTags(Set.of("tag1", "tag2"), postId);
        }
        // When
        // The order of posts was sorted descendant, so indexes will be reversed
        List<FeedPostDto> feedPostDtos = postRepository.findAllPosts(0, 100);
        // Then
        assertEquals(10, feedPostDtos.size());
        List<Long> reversedIds = ids.reversed();
        for (int i = 0; i < 10; ++i) {
            FeedPostDto feedPostDto = feedPostDtos.get(i);
            long k = reversedIds.get(i);
            long n = 10 - i;
            Assertions.assertAll(
                    () -> assertEquals(k, feedPostDto.getId()),
                    () -> assertEquals(TEST_TITLE + n, feedPostDto.getTitle()),
                    () -> assertEquals(TEST_DESCRIPTION + n, feedPostDto.getDescription()),
                    () -> assertEquals(TEST_IMAGE, feedPostDto.getImageUrl()),
                    () -> assertEquals(1, feedPostDto.getCommentsCount()),
                    // we don't mind if the order of tags is different
                    () -> assertTrue(Set.of("tag1, tag2", "tag2, tag1").contains(feedPostDto.getTags())),
                    () -> assertEquals(20, feedPostDto.getLikesCount())
            );
        }
    }

    @Test
    void update() {
        // Given
        Post firstPost =  getExpectedPost(1L, 1, LocalDateTime.now().minusHours(1));
        postRepository.save(firstPost);
        // When
        postRepository.update(getExpectedPost(1L, 2, LocalDateTime.now()));
        // Then
        Optional<Post> optionalPost = postRepository.findPostById(1L);
        optionalPost.ifPresent(actualPost ->
                Assertions.assertAll(
                        () -> assertEquals(1, actualPost.id()),
                        () -> assertEquals(TEST_TITLE + 2, actualPost.title()),
                        () -> assertEquals(TEST_DESCRIPTION + 2, actualPost.description()),
                        () -> assertEquals(TEST_CONTENT + 2, actualPost.content()),
                        () -> assertEquals(TEST_IMAGE, actualPost.imageUrl()),
                        () -> assertEquals(firstPost.createdAt().toEpochSecond(ZoneOffset.UTC),
                                actualPost.createdAt().minusHours(1).toEpochSecond(ZoneOffset.UTC))
                )
        );
    }

    @Test
    void delete() {
        deleteFromTables(jdbcTemplate, "posts");
        // Given
        long postId1 = postRepository.save(getExpectedPost(1L));
        postRepository.save(getExpectedPost(2L));
        int num = countRowsInTable(this.jdbcTemplate, "posts");
        assertEquals(2, num);
        // When
        postRepository.delete(postId1);
        // Then
        num = countRowsInTable(this.jdbcTemplate, "posts");
        assertEquals(1, num);
    }

    @Test
    void addComment() {
        // Given
        postRepository.save(getExpectedPost(1L));
        CommentDto comment = getComment(1);
        // When
        postRepository.addComment(comment);
        // Then
        List<CommentDto> comments = postRepository.findCommentsByPostId(1L);
        assertEquals(1, comments.size());
        assertEquals(1L, comments.getFirst().getPostId());
        assertEquals("This is comment content for post id = " + 1, comments.getFirst().getContent());
    }

    @Test
    void updateComment() {
        // Given
        long postId = postRepository.save(getExpectedPost(1L));
        CommentDto comment = getComment(postId);
        long id = postRepository.addComment(comment);
        // When
        postRepository.updateComment(getComment(id, postId, "Updated content"));
        // Then
        List<CommentDto> comments = postRepository.findCommentsByPostId(postId);
        assertEquals(1, comments.size());
        assertEquals(postId, comments.getFirst().getPostId());
        assertEquals(id, comments.getFirst().getId());
        assertEquals("Updated content", comments.getFirst().getContent());
    }

    @Test
    void deleteComment() {
        // Given
        long postId = postRepository.save(getExpectedPost(1L));
        CommentDto comment = getComment(postId);
        long id = postRepository.addComment(comment);
        // When
        List<CommentDto> comments = postRepository.findCommentsByPostId(postId);
        // Then
        assertEquals(1, comments.size());
        // When
        postRepository.deleteComment(id);
        // Then
        List<CommentDto> result = postRepository.findCommentsByPostId(postId);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteTagsForPostId() {
        // Given
        long postId = postRepository.save(getExpectedPost(1L));
        Set<String> tags = Set.of("tag1", "tag2");
        postRepository.insertTags(tags, postId);
        // When
        Set<String> existingTags = postRepository.findTagsByPostId(postId);
        // Then
        assertEquals(tags, existingTags);
        // When
        postRepository.deleteTagsForPostId(postId);
        // Then
        Set<String> result = postRepository.findTagsByPostId(postId);
        assertEquals(0, result.size());
    }

    @Test
    void getAllTags() {
        // Given
        long postId = postRepository.save(getExpectedPost(1L));
        Set<String> tagSet1 = Set.of("tag1", "tag2");
        Set<String> tagSet2 = Set.of("tag1", "tag3");
        postRepository.insertTags(tagSet1, postId);
        postRepository.insertTags(tagSet2, postId);
        // When
        Set<String> result = postRepository.getAllTags();
        // Then
        assertEquals(Set.of("tag1", "tag2", "tag3"), result);
    }


    @Test
    void findPostsByTag() {
        // Given
        long postId1 = postRepository.save(getExpectedPost(1L));
        long postId2 = postRepository.save(getExpectedPost(2L));
        long postId3 = postRepository.save(getExpectedPost(3L));
        Set<String> tagSet1 = Set.of("tag1", "tag2");
        Set<String> tagSet2 = Set.of("tag1", "tag3");
        Set<String> tagSet3 = Set.of("tag2", "tag3");
        postRepository.insertTags(tagSet1, postId1);
        postRepository.insertTags(tagSet2, postId2);
        postRepository.insertTags(tagSet3, postId3);
        // When
        List<FeedPostDto> posts1 = postRepository.findPostsByTag("tag1", 0, 10);
        List<FeedPostDto> posts2 = postRepository.findPostsByTag("tag2", 0, 10);
        List<FeedPostDto> posts3 = postRepository.findPostsByTag("tag3", 0, 10);
        // Then
        assertEquals(2, posts1.size());
        assertEquals(2, posts2.size());
        assertEquals(2, posts3.size());
        // And
        assertEquals(Set.of(postId1, postId2), posts1.stream().map(FeedPostDto::getId).collect(Collectors.toSet()));
        assertEquals(Set.of(postId1, postId3), posts2.stream().map(FeedPostDto::getId).collect(Collectors.toSet()));
        assertEquals(Set.of(postId2, postId3), posts3.stream().map(FeedPostDto::getId).collect(Collectors.toSet()));

    }

    @Test
    void getTotalPostsCount() {
        // Given
        IntStream.range(0, 10).forEach(i -> postRepository.save(getExpectedPost(i)));
        long num = postRepository.getTotalPostsCount();
        assertEquals(10, num);
    }


// Private
    private static Post getExpectedPost(long id, long index, LocalDateTime createdAt) {
        return new Post(id,
                TEST_TITLE + index,
                TEST_DESCRIPTION + index,
                TEST_CONTENT + index,
                TEST_IMAGE,
                createdAt);
    }

    private static Post getExpectedPost(long index) {
        return getExpectedPost(index, index, LocalDateTime.now());
    }

    private static CommentDto getComment(long id, long postId, String content) {
        CommentDto comment = new CommentDto();
        comment.setId(id);
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    private static CommentDto getComment(long postId) {
        return getComment(-1, postId, "This is comment content for post id = " + postId);
    }
}
