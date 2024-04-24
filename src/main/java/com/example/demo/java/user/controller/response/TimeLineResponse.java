package com.example.demo.java.user.controller.response;

import com.example.demo.java.tweet.controller.response.TweetResponse;

import java.util.List;

@Builder
public class TimeLineResponse {

    private String userName;

    private List<TweetResponse> followedTweets;

}
