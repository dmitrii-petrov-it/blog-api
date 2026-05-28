package org.example.blogapi.comment;

import org.example.blogapi.comment.dto.CommentRequest;
import org.example.blogapi.comment.dto.CommentResponse;
import org.example.blogapi.exception.ResourceNotFoundException;
import org.example.blogapi.post.Post;
import org.example.blogapi.post.PostRepository;
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
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Post testPost;
    private Comment testComment;

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

        testComment = Comment.builder()
                .id(100L)
                .content("Test comment")
                .author(testUser)
                .post(testPost)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("create should save comment when post and user exist")
    void create_shouldSaveComment_whenPostAndUserExist() {
        // Arrange
        CommentRequest request = new CommentRequest();
        request.setContent("Nice post!");

        CommentResponse expectedResponse = CommentResponse.builder()
                .id(100L)
                .content("Nice post!")
                .authorUsername("petr")
                .postId(10L)
                .build();

        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByUsername("petr")).thenReturn(Optional.of(testUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        when(commentMapper.toResponse(testComment)).thenReturn(expectedResponse);

        // Act
        CommentResponse result = commentService.create(10L, request, "petr");

        // Assert
        assertNotNull(result);
        assertEquals("petr", result.getAuthorUsername());
        assertEquals(10L, result.getPostId());
        verify(commentRepository).save(any(Comment.class));
    }
    @Test
    @DisplayName("create should throw exception when post not found")
    void create_shouldThrowException_whenPostNotFound() {
        // Arrange
        CommentRequest request = new CommentRequest();
        request.setContent("Hi");

        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.create(999L, request, "petr"));

        verify(commentRepository, never()).save(any());
    }
    @Test
    @DisplayName("delete should succeed when user is author")
    void delete_shouldSucceed_whenUserIsAuthor() {
        // Arrange
        when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));

        // Act — petr is the author
        commentService.delete(100L, "petr", false);

        // Assert
        verify(commentRepository).delete(testComment);
    }
    @Test
    @DisplayName("delete should succeed when user is admin")
    void delete_shouldSucceed_whenUserIsAdmin() {
        // Arrange
        when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));

        // Act — "ivan" not author, but admin
        commentService.delete(100L, "ivan", true);

        // Assert
        verify(commentRepository).delete(testComment);
    }
    @Test
    @DisplayName("delete should throw AccessDeniedException when not author and not admin")
    void delete_shouldThrowAccessDenied_whenNotAuthorAndNotAdmin() {
        // Arrange
        when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> commentService.delete(100L, "ivan", false));

        verify(commentRepository, never()).delete(any());
    }
    @Test
    @DisplayName("getByPostId should throw exception when post not found")
    void getByPostId_shouldThrowException_whenPostNotFound() {
        // Arrange
        when(postRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.getByPostId(999L, org.springframework.data.domain.Pageable.unpaged()));

        verify(commentRepository, never()).findAllByPostId(any(), any());
    }
}