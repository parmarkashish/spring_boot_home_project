package io.yume.reddit.service;

import io.yume.reddit.dto.VoteDto;
import io.yume.reddit.exceptions.PostNotFoundException;
import io.yume.reddit.exceptions.SpringRedditException;
import io.yume.reddit.model.Post;
import io.yume.reddit.model.User;
import io.yume.reddit.model.Vote;
import io.yume.reddit.repository.PostRepository;
import io.yume.reddit.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import io.yume.reddit.model.VoteType;

@Service
@AllArgsConstructor
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    public void vote(VoteDto voteDto){
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(voteDto.getPostId().toString()));
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,authService.getCurrentUser());
        if(voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())){
            throw new SpringRedditException("You have already "
                    + voteDto.getVoteType() + "'d for this post");
        }
        if(VoteType.UPVOTE.equals(voteDto.getVoteType())){
            post.setVoteCount(post.getVoteCount() + 1);
        }
        else{
            post.setVoteCount(post.getVoteCount() - 1);
        }

        voteRepository.save(map(voteDto, authService.getCurrentUser() , post));
        postRepository.save(post);



    }

    private Vote map(VoteDto voteDto, User user, Post post){
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(user)
                .build();
    }
}
