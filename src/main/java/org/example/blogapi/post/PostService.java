package org.example.blogapi.post;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.blogapi.exception.ResourceNotFoundException;
import org.example.blogapi.post.dto.PostRequest;
import org.example.blogapi.post.dto.PostResponse;
import org.example.blogapi.user.Role;
import org.example.blogapi.user.User;
import org.example.blogapi.user.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;

    public List<PostResponse> getAll(){
       return postRepository.findAll()
                .stream()
                .map(postMapper::toResponse)
                .toList();


    }

    public PostResponse getById(Long id){
      Post post =  postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return postMapper.toResponse(post);
    }

    public PostResponse create(PostRequest request,String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

       Post saved =  postRepository.save(post);
        return postMapper.toResponse(saved);
    }

    public void delete(Long id, String username, boolean isAdmin) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        boolean isAuthor = post.getAuthor().getUsername().equals(username);

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }

    public PostResponse update(Long id, PostRequest request, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        boolean isAuthor = post.getAuthor().getUsername().equals(username);


        if (!isAuthor) {
            throw new AccessDeniedException("You can only update your own posts");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        Post updated = postRepository.save(post);
        return postMapper.toResponse(updated);
    }
}
