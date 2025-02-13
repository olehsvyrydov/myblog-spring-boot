package org.javaprojects.myblogsite.repositories;

import org.javaprojects.myblogsite.models.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@JdbcTest
@Import(JdbcPostRepository.class)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment env;

    @Test
    void printProperties() {
        System.out.println(env.getProperty("spring.sql.init.schema-locations"));
        System.out.println(env.getProperty("spring.sql.init.mode"));
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM tags");
        jdbcTemplate.execute("DELETE FROM likes");
    }

    @Test
    void findPostById() {
        Post expectedPost = new Post(1L,
                "Test Title",
                "Short description",
                "This is content",
                "/images/_default_image.jpg",
                LocalDateTime.now());
        postRepository.save(expectedPost);
        Optional<Post> optionalPost = postRepository.findPostById(1L);
        optionalPost.ifPresent(actualPost ->
            Assertions.assertAll(
                    () -> assertEquals(expectedPost.id(), actualPost.id()),
                    () -> assertEquals(expectedPost.title(), actualPost.title()),
                    () -> assertEquals(expectedPost.description(), actualPost.description()),
                    () -> assertEquals(expectedPost.content(), actualPost.content()),
                    () -> assertEquals(expectedPost.imageUrl(), actualPost.imageUrl()),
                    () -> assertEquals(expectedPost.createdAt().toEpochSecond(ZoneOffset.UTC), actualPost.createdAt().toEpochSecond(ZoneOffset.UTC))
            )
        );
    }

    @Test
    void findAllPosts() {
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void addComment() {
    }

    @Test
    void updateComment() {
    }

    @Test
    void deleteComment() {
    }

    @Test
    void findCommentsByPostId() {
    }

    @Test
    void getCommentsNumberByPost() {
    }

    @Test
    void insertTags() {
    }

    @Test
    void deleteTagsForPostId() {
    }

    @Test
    void getAllTags() {
    }

    @Test
    void findTagsByPostId() {
    }

    @Test
    void findPostsByTag() {
    }

    @Test
    void addLike() {
    }

    @Test
    void getLikesNumberByPostId() {
    }

    @Test
    void getTotalPostsCount() {
    }
}
