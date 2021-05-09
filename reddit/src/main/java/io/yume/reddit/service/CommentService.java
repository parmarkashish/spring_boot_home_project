package io.yume.reddit.service;

import io.yume.reddit.dto.CommentsDto;
import io.yume.reddit.exceptions.PostNotFoundException;
import io.yume.reddit.mapper.CommentMapper;
import io.yume.reddit.model.NotificationEmail;
import io.yume.reddit.model.Post;
import io.yume.reddit.model.User;
import io.yume.reddit.repository.CommentRepository;
import io.yume.reddit.repository.PostRepository;
import io.yume.reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AuthService authService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Transactional
    public void save(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));
        commentRepository.save(commentMapper.map(commentsDto, authService.getCurrentUser() , post));
        mailService.sendMail(new NotificationEmail(authService.getCurrentUser().getUsername() + " Commented on your post",
                post.getUser().getUsername(),
                authService.getCurrentUser().getUsername() + " posted a comment on your post." + post.getUrl()));
    }

    @Transactional
    public List<CommentsDto> getCommentsByPost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId.toString()));
        return commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CommentsDto> getCommentsByUser(String userName){
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
