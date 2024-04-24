package com.example.demo.java.tweet.service;

import com.example.demo.java.exception.ItemNotFoundException;
import com.example.demo.java.tweet.controller.request.CreateTweetRequest;
import com.example.demo.java.tweet.entity.Tweet;
import com.example.demo.java.tweet.repository.TweetRepository;
import com.example.demo.java.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static java.time.Instant.now;

@Service
@AllArgsController
public class TweetService {

    private final UserService userService;

    private final TweetRepository tweetRepository;

    public Tweet post(String userId, CreateTweetRequest request) throws ItemNotFoundException {
        if(userService.findById(userId).isEmpty()) {
            throw new ItemNotFoundException(String.format("The user %s doesn't exist.", userId));
        }

        return tweetRepository.save(Tweet.builder.message(request.getMessage()).userId(request.getUserId()).publicationDate(now()).build());
    }

    public List<Tweet> findFollowedTweets(List<String> followedUserIds) {
        return tweetRepository.findAllByUserIdInOrderByPublicationDate(followedUserIds);
    }

}
