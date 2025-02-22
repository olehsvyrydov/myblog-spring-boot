package org.javaprojects.myblogsite.controllers;

import org.javaprojects.myblogsite.dto.CommentDto;
import org.javaprojects.myblogsite.dto.FeedPostDto;
import org.javaprojects.myblogsite.dto.PostDto;
import org.javaprojects.myblogsite.services.DefaultPostService;
import org.javaprojects.myblogsite.services.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(path = {"/"})
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;


    @Autowired
    public PostController(DefaultPostService postService) {
        this.postService = postService;
    }

    /**
     * All posts get request
     *
     * @param offset    is element of pagination. It shows where we should start.
     * @param limit     is element of pagination. It shows how much posts we should return
     * @param tagFilter is element of tag selection. It shows which tag we should filter by
     * @param model     Model
     * @return `feed` view
     */
    @GetMapping(value = {"/", "/posts"})
    public String getAllPosts(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "tag-filter", defaultValue = "all") String tagFilter,
            Model model) {
        long totalPosts = postService.getTotalPostsCount();
        Set<String> allTags = postService.findAllTags();
        List<FeedPostDto> posts;
        if (!tagFilter.equals("all")) {
            posts = postService.findPostsByTagName(tagFilter, offset, limit);
        } else {
            posts = postService.findAllPosts(offset, limit);
        }

        model.addAttribute("posts", posts);
        model.addAttribute("offset", offset);
        model.addAttribute("allTags", allTags);
        model.addAttribute("limit", limit);
        model.addAttribute("totalPosts", totalPosts);
        return "feed";
    }

    /**
     * Post get request
     *
     * @param id    post id
     * @param model Model
     * @return `post` view
     */
    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable("id") Long id, Model model) {
        PostDto post = postService.findPostById(id);
        long likesCount = postService.getLikes(id);

        model.addAttribute("post", post);
        model.addAttribute("likesCount", likesCount);

        return "post";
    }

    /**
     * Post create request
     *
     * @param post Dto for collection of data to create a {@link org.javaprojects.myblogsite.models.Post} and tags for it
     * @return redirection to main page
     */
    @PostMapping(value = "/posts", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createPost(@ModelAttribute PostDto post) {
        logger.info("Creating PostDto: Title: {}, Content: {}, Image: {}, Tags: {}"
                , post.getTitle()
                , post.getContent()
                , post.getImage().getOriginalFilename()
                , post.getTags());

        postService.createPost(post);
        return "redirect:/posts";
    }

    /**
     * Post update request
     *
     * @param postId post id of Post that will be updated
     * @param post   Dto for collection of data to update a {@link org.javaprojects.myblogsite.models.Post} and tags for it
     * @return redirect to updated post's page
     */
    @PostMapping(value = "/posts/{id}", params = "_method=put")
    public String updatePost(@PathVariable("id") Long postId, @ModelAttribute PostDto post) {
        logger.info("Updating post: {}", post);
        postService.updatePost(post);
        return "redirect:/posts/" + postId;
    }

    /**
     * Post delete request
     *
     * @param postId post id of Post that will be deleted
     * @return redirect to main page.
     */
    @PostMapping(value = "/posts/{id}", params = "_method=delete")
    public String deletePost(@PathVariable(name = "id") Long postId) {
        postService.deletePost(postId);
        return "redirect:/posts";
    }

    /**
     * Add likes to the post
     *
     * @param postId id of post to add likes
     * @return likes number for post with postId
     */
    @GetMapping(value = "/posts/{id}/like", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String addLike(@PathVariable("id") Long postId) {
        postService.addLike(postId);
        return String.valueOf(postService.getLikes(postId));
    }

    /**
     * Adds comment to concrete Post
     *
     * @param postId  is post id where the comment will be added
     * @param comment contains data to add
     * @return redirect to page with post where the comment has been added
     */
    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable("id") Long postId, @ModelAttribute CommentDto comment) {
        logger.info("Adding comment:Start. Comment: {}", comment.getContent());
        if (comment.getContent().trim().isEmpty()) {
            logger.info("Adding comment: Content is empty");
            return "redirect:/posts/" + postId;
        }
        comment.setPostId(postId);
        logger.info("Adding comment: PostId: {}, Content: {}", comment.getPostId(), comment.getContent());
        postService.addComment(comment);
        return "redirect:/posts/" + postId;
    }

    /**
     * Adds comment to concrete Post
     *
     * @param postId    is post id where the comment will be added
     * @param commentId is id of the comment where the update will be added
     * @param comment   contains data to add
     * @return redirect to page with post where the comment has been added
     */
    @PostMapping("/posts/{id}/comments/{commentId}")
    public String updateComment(@PathVariable("id") Long postId, @PathVariable("commentId") Long commentId, @ModelAttribute CommentDto comment) {
        logger.info("Updating comment:Start");
        if (comment.getContent().trim().isEmpty()) {
            logger.info("Updating comment: Content is empty");
            return "redirect:/posts/" + postId;
        }
        logger.info("Updating comment: {}", comment);
        comment.setPostId(postId);
        comment.setId(commentId);
        postService.updateComment(comment);
        return "redirect:/posts/" + postId;
    }

    /**
     * Deletes a comment from the post
     *
     * @param postId    is post id where the comment will be deleted from
     * @param commentId is id of the comment to delete
     * @param comment   contains log information about content of deleting
     * @return ok status to javascript control after deleting
     */
    @DeleteMapping(value = "/posts/{id}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long postId, @PathVariable("commentId") Long commentId, @ModelAttribute CommentDto comment) {
        logger.info("Deleting comment: {}", comment);
        postService.deleteComment(commentId);
        return ResponseEntity.ok().body("Deleted sucessfully");
    }
}
