package org.example.blogapi.comment;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.blogapi.comment.dto.CommentRequest;
import org.example.blogapi.comment.dto.CommentResponse;
import org.example.blogapi.exception.ResourceNotFoundException;
import org.example.blogapi.post.Post;
import org.example.blogapi.post.PostRepository;
import org.example.blogapi.user.User;
import org.example.blogapi.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @Transactional(readOnly = true)
    public Page<CommentResponse> getByPostId(Long postId, Pageable pageable){
        if(!postRepository.existsById(postId)){
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        return commentRepository.findAllByPostId(postId,pageable)
                .map(commentMapper::toResponse);
    }

    @Transactional
    public CommentResponse create(Long postId, CommentRequest request,String username){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        Comment comment = Comment.builder()
                .content(request.getContent())
                .author(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
       Comment saved =  commentRepository.save(comment);
       return commentMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id,String username,boolean isAdmin)  {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        boolean isAuthor = comment.getAuthor().getUsername().equals(username);
        if(!isAdmin && !isAuthor)
        {
            throw new AccessDeniedException("You can only delete your own comments");
        }
        commentRepository.delete(comment);

    }






}
