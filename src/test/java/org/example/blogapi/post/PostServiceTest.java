package org.example.blogapi.post;

import org.example.blogapi.exception.ResourceNotFoundException;
import org.example.blogapi.post.dto.PostRequest;
import org.example.blogapi.post.dto.PostResponse;
import org.example.blogapi.user.Role;
import org.example.blogapi.user.User;
import org.example.blogapi.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("petr")
                .email("petr@test.com")
                .password("hashed")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        testPost = Post.builder()
                .id(10L)
                .title("Test post")
                .content("Test content")
                .author(testUser)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getById should return PostResponse when post exists")
    void getById_shouldReturnPostResponse_whenPostExists() {
        // Arrange
        PostResponse expectedResponse = PostResponse.builder()
                .id(10L)
                .title("Test post")
                .authorUsername("petr")
                .build();

        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(postMapper.toResponse(testPost)).thenReturn(expectedResponse);

        // Act
        PostResponse result = postService.getById(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("petr", result.getAuthorUsername());

        verify(postRepository).findById(10L);
        verify(postMapper).toResponse(testPost);
    }

    @Test
    @DisplayName("getById should throw ResourceNotFoundException when post does not exist")
    void getById_shouldThrowException_whenPostDoesNotExist() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> postService.getById(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(postRepository).findById(999L);
        verify(postMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("create should save post and return response")
    void create_shouldSavePost_whenUserExists() {
        // Arrange
        PostRequest request = new PostRequest();
        request.setTitle("New post");
        request.setContent("New content");

        PostResponse expectedResponse = PostResponse.builder()
                .id(10L)
                .title("New post")
                .authorUsername("petr")
                .build();

        when(userRepository.findByUsername("petr")).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(postMapper.toResponse(testPost)).thenReturn(expectedResponse);

        // Act
        PostResponse result = postService.create(request, "petr");

        // Assert
        assertNotNull(result);
        assertEquals("petr", result.getAuthorUsername());
        verify(userRepository).findByUsername("petr");
        verify(postRepository).save(any(Post.class));
    }
    @Test
    @DisplayName("create should throw exception when user not found")
    void create_shouldThrowException_whenUserNotFound() {
        // Arrange
        PostRequest request = new PostRequest();
        request.setTitle("New post");
        request.setContent("New content");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> postService.create(request, "ghost"));

        verify(postRepository, never()).save(any());
    }
    @Test
    @DisplayName("update should succeed when user is author")
    void update_shouldSucceed_whenUserIsAuthor() {
        // Arrange
        PostRequest request = new PostRequest();
        request.setTitle("Updated");
        request.setContent("Updated content");

        PostResponse expectedResponse = PostResponse.builder()
                .id(10L)
                .title("Updated")
                .authorUsername("petr")
                .build();

        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(postMapper.toResponse(testPost)).thenReturn(expectedResponse);

        // Act — petr is the author
        PostResponse result = postService.update(10L, request, "petr");

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.getTitle());
        verify(postRepository).save(testPost);
    }
    @Test
    @DisplayName("update should throw AccessDeniedException when user is not author")
    void update_shouldThrowAccessDenied_whenNotAuthor() {
        // Arrange
        PostRequest request = new PostRequest();
        request.setTitle("Hacked");
        request.setContent("Hacked content");

        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));

        // Act & Assert — "ivan" is not the author
        assertThrows(AccessDeniedException.class,
                () -> postService.update(10L, request, "ivan"));

        verify(postRepository, never()).save(any());
    }
    @Test
    @DisplayName("delete should succeed when user is admin")
    void delete_shouldSucceed_whenUserIsAdmin() {
        // Arrange
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));

        // Act — "ivan" is not author, BUT is admin
        postService.delete(10L, "ivan", true);

        // Assert
        verify(postRepository).delete(testPost);
    }

    @Test
    @DisplayName("delete should throw ResourceNotFoundException when post not found")
    void delete_shouldThrowNotFound_whenPostNotFound() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> postService.delete(999L, "petr", false));

        verify(postRepository, never()).delete(any());
    }

    @Test
    @DisplayName("update should throw ResourceNotFoundException when post not found")
    void update_shouldThrowNotFound_whenPostNotFound() {
        // Arrange
        PostRequest request = new PostRequest();
        request.setTitle("Title");
        request.setContent("Content");

        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> postService.update(999L, request, "petr"));

        verify(postRepository, never()).save(any());
    }

}