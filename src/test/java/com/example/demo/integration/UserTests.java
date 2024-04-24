package com.example.demo.integration;

import com.example.demo.java.exception.ApiError;
import com.example.demo.java.exception.ApiSubError;
import com.example.demo.java.tweet.entity.Tweet;
import com.example.demo.java.tweet.repository.TweetRepository;
import com.example.demo.java.user.controller.request.CreateUserRequest;
import com.example.demo.java.user.controller.response.TimeLineResponse;
import com.example.demo.java.user.controller.response.UserResponse;
import com.example.demo.java.user.entity.User;
import com.example.demo.java.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Test
    @DisplayName("Given a valid request to create a user, one is created and with a 201 status")
    void createUserOk() throws Exception {
        var request = CreateUserRequest.builder().userName("trini").password("test").build();

        var response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userName").exists())
                .andExpect(jsonPath("$.followedUserIds", hasSize(0)))
                .andExpect(status().isCreated())
                .andReturn();

        var createdUser = objectMapper.readValue(response.getResponse().getContentAsString(), UserResponse.class);
        var expectedUser = UserResponse.builder().id(createdUser.getId()).userName("trini").followedUserIds(new ArrayList<>()).build();

        assertEquals(expectedUser, createdUser);

        userRepository.deleteById(createdUser.getId());
    }

    @Test
    @DisplayName("Given an invalid request to create a user, bad request status 403 is returned")
    void createUserBadRequest() throws Exception {
        var request = CreateUserRequest.builder().build();
        var response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        var apiError = objectMapper.readValue(response.getResponse().getContentAsString(), ApiError.class);

        var expectedError = ApiError.builder().status(BAD_REQUEST).message("Validation Error").subErrors(List.of(
                ApiSubError.builder()
                        .object("createUserRequest")
                        .field("password")
                        .message("must not be blank")
                        .build(),
                ApiSubError.builder()
                        .object("createUserRequest")
                        .field("userName")
                        .message("must not be blank")
                        .build()
        )).build();

        assertEquals(apiError.getStatus(), expectedError.getStatus());
        assertEquals(apiError.getMessage(), expectedError.getMessage());
        assertTrue(apiError.getSubErrors().containsAll(expectedError.getSubErrors()));
    }

    @Test
    @DisplayName("Given a valid userId and another valid one as a userId to follow, the first user will add the second user as followedUser and return a notContent 204 status")
    void followUser() throws Exception {
        var followingUser = userRepository.save(User.builder().userName("trini").password("pass").build());
        var followedUser = userRepository.save(User.builder().userName("followed").password("pass").build());

        mockMvc.perform(post(String.format("/users/%s/follow/%s", followingUser.getId(), followedUser.getId())))
                .andExpect(status().isNoContent());

        var followingUserAfterFollow = userRepository.findById(followingUser.getId()).orElseThrow();

        assertTrue(followingUserAfterFollow.getFollowedUserIds().contains(followedUser.getId()));

        userRepository.deleteById(followingUser.getId());
        userRepository.deleteById(followedUser.getId());
    }

    @Test
    @DisplayName("Given a valid userId, the timeline with all the followed users' tweets is returned in chronological order")
    void userTimeline() throws Exception {
        var existingUser1 = userRepository.save(User.builder().userName("first").password("pass").build());
        var firstTweet = tweetRepository.save(Tweet.builder().message("first message").userId(existingUser1.getId()).publicationDate(now().minus(1, DAYS)).build());
        var secondTweet = tweetRepository.save(Tweet.builder().message("second message").userId(existingUser1.getId()).publicationDate(now().minus(2, DAYS)).build());

        var existingUser2 = userRepository.save(User.builder().userName("second").password("pass").build());
        var thirdTweet = tweetRepository.save(Tweet.builder().message("third message").userId(existingUser2.getId()).publicationDate(now().minus(3, DAYS)).build());
        var lastTweet = tweetRepository.save(Tweet.builder().message("last message").userId(existingUser2.getId()).publicationDate(now().minus(4, DAYS)).build());

        var timeLineUser = userRepository.save(User.builder().userName("trini").password("pass").followedUserIds(List.of(existingUser1.getId(), existingUser2.getId())).build());

        var response = mockMvc.perform(get(String.format("/users/%s/timeline", timeLineUser.getId())))
                .andExpect(status().isOk())
                .andReturn();

        var timeLine = objectMapper.readValue(response.getResponse().getContentAsString(), TimeLineResponse.class);

        assertEquals("trini", timeLine.getUserName());
        assertEquals(firstTweet.getId(), timeLine.getFollowedTweets().get(0).getId());
        assertEquals(secondTweet.getId(), timeLine.getFollowedTweets().get(1).getId());
        assertEquals(thirdTweet.getId(), timeLine.getFollowedTweets().get(2).getId());
        assertEquals(lastTweet.getId(), timeLine.getFollowedTweets().get(3).getId());

        userRepository.deleteById(existingUser1.getId());
        userRepository.deleteById(existingUser2.getId());
        userRepository.deleteById(timeLineUser.getId());
        tweetRepository.deleteById(firstTweet.getId());
        tweetRepository.deleteById(secondTweet.getId());
        tweetRepository.deleteById(thirdTweet.getId());
        tweetRepository.deleteById(lastTweet.getId());
    }

}
