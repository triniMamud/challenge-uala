package com.example.demo.java.tweet.controller;

import com.example.demo.java.tweet.controller.request.CreateTweetRequest;
import com.example.demo.java.tweet.controller.response.TweetResponse;
import com.example.demo.java.tweet.service.TweetService;

@RestController("/tweet")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<TweetResponse> post(@RequestBody CreateTweetRequest request) {
        var createdTweet = tweetService.post(userId, request);

       return new ResponseEntity<>(modelMapper.map(createdTweet, TweetResponse.class), HttpStatus.CREATED);
    }

}
