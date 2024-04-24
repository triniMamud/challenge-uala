package com.example.demo.java.tweet.repository;

import com.example.demo.java.tweet.entity.Tweet;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<String, Tweet> {

    List<Tweet> findAllByUserIdInOrderByPublicationDate(List<String> userIds);

}
