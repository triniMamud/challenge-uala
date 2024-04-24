package com.example.demo.java.user.controller;

import com.example.demo.java.tweet.controller.response.TweetResponse;
import com.example.demo.java.tweet.service.TweetService;
import com.example.demo.java.user.controller.request.CreateUserRequest;
import com.example.demo.java.user.controller.response.TimeLineResponse;
import com.example.demo.java.user.controller.response.UserResponse;
import com.example.demo.java.user.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final TweetService tweetService;

    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        var createdUser = userService.create(request);

        return new ResponseEntity<>(modelMapper.map(createdUser, UserResponse.class), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/follow/{userId}")
    public ResponseEntity<Void> follow(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        userService.followUser(id, userId);

        return noConent.build();
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<TimeLineResponse> getTimeline(@PathVariable("id") String id) {
        var user = userService.get(id);
        var followedTweets = tweetService.findFollowedTweets(user.getFollowedUser()).stream()
                .map(tweet -> modelMapper.map(tweet, TweetResponse.class)).toList();

        var response = TimeLineResponse.builder.userName(user.getName()).followedTweets(followedTweets).build();
        
        return ok(response);
    }

}
