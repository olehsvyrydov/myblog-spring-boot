package org.javaprojects.myblogsite.controllers;

import org.javaprojects.myblogsite.dto.FeedPostDto;
import org.javaprojects.myblogsite.models.Post;
import org.javaprojects.myblogsite.repositories.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PostControllerTest {
    public static final MockMultipartFile IMAGE_FILE = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "bla-bla".getBytes());
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    PostRepository postRepository;

    @Test
    void getAllPosts_goingToBasePath_shouldReturnAllPosts() throws Exception {
        addPostRecord(0, 10);

        mockMvc.perform(get("/")
                        .param("offset", String.valueOf(0))
                        .param("limit", String.valueOf(10))
                        .param("tagFilter", "all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("offset"))
                .andExpect(model().attributeExists("limit"))
                .andExpect(model().attributeExists("allTags"))
                .andExpect(model().attributeExists("totalPosts"))
                .andExpect(xpath("//section[@class='posts-list']//tr/td").nodeCount(10));
    }

    @Test
    void getAllPosts_shouldReturnAllPosts() throws Exception {
        addPostRecord(10, 50);

        mockMvc.perform(get("/posts")
                        .param("offset", String.valueOf(10))
                        .param("limit", String.valueOf(50))
                        .param("tagFilter", "all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("offset"))
                .andExpect(model().attributeExists("limit"))
                .andExpect(model().attributeExists("allTags"))
                .andExpect(model().attributeExists("totalPosts"))
                .andExpect(xpath("//section[@class='posts-list']//tr/td").nodeCount(40));
    }

    @Test
    void getPostById_shouldBuildPostPage() throws Exception {
        when(postRepository.findPostById(anyLong())).thenReturn(Optional.of(new Post(1L,
                "title",
                "desc",
                "content",
                "image",
                LocalDateTime.now())));
        when(postRepository.findTagsByPostId(anyLong())).thenReturn(Set.of("tag1", "tag2"));

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("likesCount"))
                .andExpect(xpath("//h2[@id='post-title']").string("title"))
                .andExpect(xpath("//img[@id='post-image']/@src").string("image"))
                .andExpect(xpath("//div[@id='post-text']").string("content"))
                .andExpect(xpath("//p[@id='post-tags']/strong").string("Tags:"))
                .andExpect(xpath("//p[@id='post-tags']/em").string("tag1, tag2"));
    }

    @Test
    void createPost_postShouldBeCreated() throws Exception {
        mockMvc.perform(multipart("/posts")
                .file(IMAGE_FILE)
                .param("title", "title")
                .param("description", "description")
                .param("content", "some content")
                .param("tags", "tag1, tag2, tag3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts"));
    }

    @Test
    void updatePost() throws Exception {
        mockMvc.perform(multipart("/posts")
                        .file(IMAGE_FILE)
                        .param("_method", "put")
                        .param("id", "1")
                        .param("title", "title")
                        .param("description", "description")
                        .param("content", "some content")
                        .param("imageUrl", "imageUrl")
                        .param("tags", "tag1, tag2, tag3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts"));
    }

    @Test
    void deletePost() throws Exception {
        mockMvc.perform(post("/posts/1").param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts"));
    }

    private List<FeedPostDto> addPostRecord(int from, int number) {
        List<FeedPostDto> posts = LongStream.range(from, number).mapToObj(
                i -> {
                    FeedPostDto post = new FeedPostDto();
                    post.setId(i);
                    post.setTitle("title");
                    post.setDescription("description");
                    post.setTags("tag1, tag2");
                    post.setCommentsCount(10L);
                    post.setLikesCount(20L);
                    post.setImageUrl(String.format("/upload/image_%s.jpg", i));
                    return post;
                }).toList();
        when(postRepository.findAllPosts(eq(from), anyInt())).thenReturn(posts);
        return posts;
    }
}
