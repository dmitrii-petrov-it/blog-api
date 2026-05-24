package org.example.blogapi.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blogapi.comment.dto.CommentRequest;
import org.example.blogapi.comment.dto.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponse>> getByPostId(
            @PathVariable Long postId,
            @PageableDefault(size = 10,sort = "createdAt",direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        return ResponseEntity.ok(commentService.getByPostId(postId,pageable));
    }

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ){
        String username = authentication.getName();
        CommentResponse response = commentService.create(postId, request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        commentService.delete(id, username, isAdmin);
        return ResponseEntity.noContent().build();
    }




}
