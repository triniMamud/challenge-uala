package com.example.demo.java.user.controller.response;

import com.example.demo.java.tweet.controller.response.TweetResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeLineResponse {

    private String userName;

    private List<TweetResponse> followedTweets;

}
