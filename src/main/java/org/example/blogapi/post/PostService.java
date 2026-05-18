package org.example.blogapi.post;

import lombok.RequiredArgsConstructor;
import org.example.blogapi.exception.ResourceNotFoundException;
import org.example.blogapi.post.dto.PostResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

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
}
