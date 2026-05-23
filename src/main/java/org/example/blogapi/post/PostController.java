package org.example.blogapi.post;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blogapi.post.dto.PostRequest;
import org.example.blogapi.post.dto.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @GetMapping()
    public ResponseEntity<Page<PostResponse>> getAll
            (@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
              Pageable pageable
            ){
        return ResponseEntity.ok(postService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(postService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(
            @Valid @RequestBody PostRequest request,
            Authentication authentication
    ){
        String username = authentication.getName();
        PostResponse response = postService.create(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        postService.delete(id, username, isAdmin);
        return ResponseEntity.noContent().build();

    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request,
            Authentication authentication
    ){
        String username = authentication.getName();

        PostResponse postResponse = postService.update(id, request, username);
        return ResponseEntity.ok(postResponse);
    }



}
