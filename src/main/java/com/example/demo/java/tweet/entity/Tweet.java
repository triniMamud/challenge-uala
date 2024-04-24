package com.example.demo.java.tweet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tweet")
public class Tweet {

    @Id
    private String id;

    private String message;

    private Instant publicationDate;

    @Indexed
    private String userId;

}
