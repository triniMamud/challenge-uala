package com.example.demo.java.tweet.entity;

import java.time.Instant;

@Builder
public class Tweet {

    private String id;

    private String message;

    private Instant publicationDate;

    private String userId;

}
