package org.javaprojects.myblogsite.repositories;

import org.javaprojects.myblogsite.dto.CommentDto;
import org.javaprojects.myblogsite.dto.FeedPostDto;
import org.javaprojects.myblogsite.models.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class JdbcPostRepository implements PostRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcPostRepository.class);
    private final JdbcClient jdbcClient;
    private final JdbcTemplate jdbcTemplate;

    JdbcPostRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcClient = JdbcClient.create(jdbcTemplate);
    }

    private static FeedPostDto mapFeedRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        String imageUrl = rs.getString("image_url");
        Long commentsCount = rs.getLong("commentsCount");
        Long likesCount = rs.getLong("likesCount");
        String tagsStr = rs.getString("tags");

        Set<String> tags = (tagsStr != null && !tagsStr.isEmpty())
                ? Set.copyOf(Arrays.asList(tagsStr.split("[,|]\\s*")))
                : Collections.emptySet();

        return FeedPostDto.builder()
                .setId(id)
                .setTitle(title)
                .setImageUrl(imageUrl)
                .setDescription(description)
                .setCommentsCount(commentsCount)
                .setLikesCount(likesCount)
                .setTags(tags)
                .create();
    }

    @Override
    public Optional<Post> findPostById(long id) {
        String sql = "SELECT id, title, description, content, image_url, created_at FROM posts WHERE id = :id";
        return jdbcClient.sql(sql)
                .params(Map.of("id", id))
                .query(Post.class)
                .optional();
    }

    @Override
    public List<FeedPostDto> findAllPosts(int from, int number) {
        String sql = """
                SELECT p.id, p.title, p.description, p.image_url, p.created_at, \
                  (SELECT count(*) FROM comments WHERE post_id = p.id) AS commentsCount, \
                  (SELECT likes_count FROM likes WHERE post_id = p.id) AS likesCount, \
                  (SELECT GROUP_CONCAT(tag_name SEPARATOR ',') FROM tags WHERE post_id = p.id) AS tags \
                FROM posts AS p \
                ORDER BY created_at DESC \
                OFFSET ? FETCH FIRST ? ROWS ONLY""";

        return jdbcClient.sql(sql)
                .params(from, number)
                .query(JdbcPostRepository::mapFeedRow)
                .list();
    }

    @Override
    public long save(Post post) {
        String sql = "INSERT INTO posts (title, description, content, image_url, created_at) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql)
                .params(
                        post.title(),
                        post.description(),
                        post.content(),
                        post.imageUrl(),
                        LocalDateTime.now()
                )
                .update(keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            return key.longValue();
        }
        return -1;
    }

    @Override
    public void update(Post post) {
        String sql = """
                    UPDATE posts
                    SET
                        title       = IFNULL(?, title),
                        description = IFNULL(?, description),
                        content     = IFNULL(?, content),
                        image_url   = IFNULL(?, image_url)
                    WHERE id = ?
                """;

        jdbcClient.sql(sql)
                .params(
                        post.title(),
                        post.description(),
                        post.content(),
                        post.imageUrl(),
                        post.id()
                )
                .update();
    }

    @Override
    public void delete(long postId) {
        jdbcClient.sql("DELETE FROM posts WHERE id = ?").params(postId).update();
    }

    @Override
    public void addComment(CommentDto comment) {
        String sql = "INSERT INTO comments (post_id, content, created_at) VALUES (?,?,?)";
        jdbcClient.sql(sql)
                .params(comment.getPostId(), comment.getContent(), comment.getCreatedAt())
                .update();
    }

    @Override
    public void updateComment(CommentDto comment) {
        String sql = "UPDATE comments SET content = ? WHERE id = ?";
        jdbcClient.sql(sql)
                .params(comment.getContent(), comment.getId())
                .update();
    }

    @Override
    public void deleteComment(long commentId) {
        int num = jdbcClient.sql("DELETE FROM comments WHERE id = ?").params(commentId).update();
        logger.info("Removed from commentd {} lines. commentId = {}", num, commentId);
    }

    @Override
    public List<CommentDto> findCommentsByPostId(long postId) {
        String sql = "SELECT id, post_id, content, created_at FROM comments WHERE post_id = ? order by created_at";

        return jdbcClient.sql(sql)
                .params(postId)
                .query(new BeanPropertyRowMapper<>(CommentDto.class))
                .list();
    }

    @Override
    public long getCommentsNumberByPost(long postId) {
        String sql = "SELECT count(*) FROM comments WHERE post_id = ?";

        return (Long) jdbcClient.sql(sql)
                .params(postId)
                .query()
                .singleValue();
    }

    @Override
    public Set<String> findTagsByPostId(long postId) {
        String sql =
                "SELECT t.tag_name FROM tags AS t LEFT JOIN posts AS p ON p.id = t.post_id WHERE p.id = ?";

        return jdbcClient.sql(sql)
                .params(postId)
                .query(String.class)
                .set();
    }

    @Override
    public List<FeedPostDto> findPostsByTag(String tagName, int from, int number) {
        String sql = """
                SELECT p.id, p.title, p.description, p.image_url, p.created_at, \
                  (SELECT count(*) FROM comments WHERE post_id = p.id) AS commentsCount, \
                  (SELECT likes_count FROM likes WHERE post_id = p.id) AS likesCount, \
                  (SELECT GROUP_CONCAT(tag_name SEPARATOR ',') FROM tags WHERE post_id = p.id) AS tags \
                FROM posts AS p \
                LEFT JOIN tags AS t ON p.id = t.post_id \
                WHERE t.tag_name = ? \
                ORDER BY created_at DESC \
                OFFSET ? FETCH NEXT ? ROWS ONLY""";

        return jdbcClient.sql(sql)
                .params(tagName, from, number)
                .query(JdbcPostRepository::mapFeedRow)
                .list();
    }

    @Override
    public void addLike(long postId) {

        String updateSql = "UPDATE likes SET likes_count = likes_count + 1 WHERE post_id = ?";
        int num = jdbcClient.sql(updateSql)
                .params(postId)
                .update();
        if (num == 0) {
            String insertSql = "INSERT INTO likes (post_id, likes_count) VALUES ( ?, 1)";
            jdbcClient.sql(insertSql)
                    .params(postId)
                    .update();
        }
    }

    @Override
    public void insertTags(Set<String> tags, long postId) {
        String sql = "INSERT INTO tags (tag_name, post_id) VALUES (?, ?)";
        List<Object[]> batchArgs = tags.stream()
                .map(tag -> new Object[]{tag, postId})
                .toList();
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public void deleteTagsForPostId(long postId) {
        String sql = "DELETE FROM tags WHERE post_id = ?";
        jdbcClient.sql(sql).params(postId).update();
    }

    @Override
    public Set<String> getAllTags() {
        String sql = "SELECT tag_name FROM tags";
        return jdbcClient.sql(sql)
                .query(String.class)
                .set();
    }

    @Override
    public long getLikesNumberByPostId(long postId) {
        String sql = "SELECT likes_count FROM likes WHERE post_id = ?";

        return (Long) jdbcClient.sql(sql)
                .params(postId)
                .query()
                .optionalValue().orElse(0L);
    }

    @Override
    public long getTotalPostsCount() {
        String sql = "SELECT count(*) FROM posts";

        return (long) jdbcClient.sql(sql)
                .query()
                .singleValue();
    }
}
