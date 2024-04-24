package com.example.demo.java.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Encrypted;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user")
@Encrypted(keyId = "xKVup8B1Q+CkHaVRx+qa+g==", algorithm = "AEAD_AES_256_CBC_HMAC_SHA_512-Random")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userName;

    @Encrypted
    private String password;

    private List<String> followedUserIds = new ArrayList<>();

}
