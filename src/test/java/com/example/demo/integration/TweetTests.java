package com.example.demo.integration;

import com.example.demo.java.exception.ApiError;
import com.example.demo.java.exception.ApiSubError;
import com.example.demo.java.tweet.controller.request.CreateTweetRequest;
import com.example.demo.java.tweet.controller.response.TweetResponse;
import com.example.demo.java.tweet.repository.TweetRepository;
import com.example.demo.java.user.entity.User;
import com.example.demo.java.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TweetTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Given a valid request to create a tweet with a valid userId, one is created and with a 201 status")
    void createUserOk() throws Exception {
        var existingUser = userRepository.save(User.builder().userName("trini").password("pass").build());

        var request = CreateTweetRequest.builder().message("this is a message").userId(existingUser.getId()).build();

        var response = mockMvc.perform(post("/tweets")
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.publicationDate").exists())
                .andExpect(status().isCreated())
                .andReturn();

        var createdTweet = objectMapper.readValue(response.getResponse().getContentAsString(), TweetResponse.class);
        var expectedTweet = TweetResponse.builder()
                .id(createdTweet.getId())
                .userId(existingUser.getId())
                .message("this is a message")
                .publicationDate(createdTweet.getPublicationDate())
                .build();

        assertEquals(expectedTweet, createdTweet);

        userRepository.deleteById(existingUser.getId());
        tweetRepository.deleteById(createdTweet.getId());
    }

    @Test
    @DisplayName("Given an invalid request to create a tweet, bad request status 403 is returned")
    void createUserBadRequest() throws Exception {
        var request = CreateTweetRequest.builder().message("aaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").build();

        var response = mockMvc.perform(post("/tweets")
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        var apiError = objectMapper.readValue(response.getResponse().getContentAsString(), ApiError.class);

        var expectedError = ApiError.builder().status(BAD_REQUEST).message("Validation Error").subErrors(List.of(
                ApiSubError.builder()
                        .object("createTweetRequest")
                        .field("message")
                        .message("size must be between 1 and 280")
                        .build(),
                ApiSubError.builder()
                        .object("createTweetRequest")
                        .field("userId")
                        .message("must not be blank")
                        .build()
        )).build();

        assertEquals(apiError.getStatus(), expectedError.getStatus());
        assertEquals(apiError.getMessage(), expectedError.getMessage());
        assertTrue(apiError.getSubErrors().containsAll(expectedError.getSubErrors()));
    }

}
