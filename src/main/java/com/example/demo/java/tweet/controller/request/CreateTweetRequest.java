package com.example.demo.java.tweet.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTweetRequest {

    @Size(min = 1, max = 280)
    private String message;

    @NotBlank
    private String userId;

}
