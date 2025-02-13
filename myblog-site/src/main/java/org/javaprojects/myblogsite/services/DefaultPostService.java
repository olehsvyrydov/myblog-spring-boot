package org.javaprojects.myblogsite.services;

import org.javaprojects.myblogsite.dto.CommentDto;
import org.javaprojects.myblogsite.dto.FeedPostDto;
import org.javaprojects.myblogsite.dto.PostDto;
import org.javaprojects.myblogsite.models.Post;
import org.javaprojects.myblogsite.repositories.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class DefaultPostService implements PostService {

    private final PostRepository postRepository;
    private final StorageService storageService;

    public DefaultPostService(PostRepository postRepository, StorageService storageService) {
        this.postRepository = postRepository;
        this.storageService = storageService;
    }

    @Override
    public List<FeedPostDto> findAllPosts(int from, int number) {
        return postRepository.findAllPosts(from, number);
    }

    @Override
    public List<FeedPostDto> findPostsByTagName(String tagName, int from, int number) {
        return postRepository.findPostsByTag(tagName, from, number);
    }

    @Override
    public PostDto findPostById(long id) {
        Post post = postRepository.findPostById(id).orElseThrow();
        Set<String> tags = postRepository.findTagsByPostId(id);
        PostDto postDto = new PostDto(post.id(), post.title(), post.description(), post.content(), post.imageUrl());
        if (!tags.isEmpty()) {
            postDto.setTags(String.join(", ", tags));
        }
        List<CommentDto> comments = findCommentsByPost(id);
        postDto.setComments(comments);
        return postDto;
    }

    @Override
    public void createPost(PostDto postDto) {
        String imageUrl = storageService.handleFileUpload(postDto.getImage());
        Post post = new Post(null,
                postDto.getTitle(),
                postDto.getDescription(),
                postDto.getContent(),
                imageUrl,
                LocalDateTime.now()
        );
        long id = postRepository.save(post);
        if (id != -1) {
            Set<String> tags = Set.of(postDto.getTags().split("[,| ]\\s*"));
            postRepository.insertTags(tags, id);
        }
    }

    @Override
    public void updatePost(PostDto postDto) {
        String imageUrl;
        if (!postDto.getImage().isEmpty()) {
            imageUrl = storageService.handleFileUpload(postDto.getImage());
        } else {
            imageUrl = null;
        }
        Post post = new Post(
                postDto.getId(),
                postDto.getTitle(),
                postDto.getDescription(),
                postDto.getContent(),
                imageUrl,
                LocalDateTime.now()
        );
        postRepository.update(post);
        Set<String> tags = Set.of(postDto.getTags().split("[,|]\\s*"));
        postRepository.deleteTagsForPostId(postDto.getId());
        postRepository.insertTags(tags, postDto.getId());
        postRepository.update(post);
    }

    @Override
    public void deletePost(Long postId) {
        postRepository.delete(postId);
    }

    @Override
    public void addComment(CommentDto commentDto) {
        commentDto.setCreatedAt(LocalDateTime.now());
        postRepository.addComment(commentDto);
    }

    @Override
    public void updateComment(CommentDto commentDto) {
        postRepository.updateComment(commentDto);
    }

    @Override
    public List<CommentDto> findCommentsByPost(long postId) {
        return postRepository.findCommentsByPostId(postId);
    }

    @Override
    public void addLike(long postId) {
        postRepository.addLike(postId);
    }

    @Override
    public long getLikes(long postId) {
        return postRepository.getLikesNumberByPostId(postId);
    }

    @Override
    public Set<String> findTagsByPostId(Long id) {
        return postRepository.findTagsByPostId(id);
    }

    @Override
    public Set<String> findAllTags() {
        return postRepository.getAllTags();
    }

    @Override
    public long getTotalPostsCount() {
        return postRepository.getTotalPostsCount();
    }

    @Override
    public void deleteComment(Long commentId) {
        postRepository.deleteComment(commentId);
    }

}
