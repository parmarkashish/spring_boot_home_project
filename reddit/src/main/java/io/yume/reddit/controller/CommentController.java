package io.yume.reddit.controller;

import io.yume.reddit.dto.CommentsDto;

import io.yume.reddit.repository.CommentRepository;
import io.yume.reddit.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/comments/")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;
    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto){
        commentService.save(commentsDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("by-postId/{postId}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsByPost(@PathVariable Long postId){
        return status(HttpStatus.OK).body(commentService.getCommentsByPost(postId));
    }

    @GetMapping("by-user/{userName}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsByUser(@PathVariable String userName){
        return status(HttpStatus.OK).body(commentService.getCommentsByUser(userName));
    }
}
