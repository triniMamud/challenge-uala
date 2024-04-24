package com.example.demo.java.tweet.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TweetResponse {

    private String id;

    private String userId;

    private String message;

    private Instant publicationDate;

}
