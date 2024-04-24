package com.example.demo.java.tweet.controller;

import com.example.demo.java.tweet.controller.request.CreateTweetRequest;
import com.example.demo.java.tweet.controller.response.TweetResponse;
import com.example.demo.java.tweet.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/tweets")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<TweetResponse> post(@RequestBody @Valid CreateTweetRequest request) {
        var createdTweet = tweetService.post(request);

        return new ResponseEntity<>(modelMapper.map(createdTweet, TweetResponse.class), CREATED);
    }

}
