package com.example.demo.java.tweet.controller.request;

public class CreateTweetRequest {

    @Size(max = 280)
    private String message;

    @NotBlank
    private String userId;

}
