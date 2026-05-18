package org.example.blogapi.post;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blogapi.post.dto.PostRequest;
import org.example.blogapi.post.dto.PostResponse;
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
    public ResponseEntity<List<PostResponse>> getAll(){
        return ResponseEntity.ok(postService.getAll());
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


}
