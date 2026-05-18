package org.example.blogapi.post;

import lombok.RequiredArgsConstructor;
import org.example.blogapi.exception.ResourceNotFoundException;
import org.example.blogapi.post.dto.PostRequest;
import org.example.blogapi.post.dto.PostResponse;
import org.example.blogapi.user.User;
import org.example.blogapi.user.UserRepository;
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
}
