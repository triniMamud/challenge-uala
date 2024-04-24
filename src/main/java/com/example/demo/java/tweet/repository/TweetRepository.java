package com.example.demo.java.tweet.repository;

import com.example.demo.java.tweet.entity.Tweet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends MongoRepository<Tweet, String> {

    List<Tweet> findAllByUserIdInOrderByPublicationDateDesc(List<String> userIds);

}
