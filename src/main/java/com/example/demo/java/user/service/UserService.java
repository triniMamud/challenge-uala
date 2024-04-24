package com.example.demo.java.user.service;

import com.example.demo.java.exception.ItemNotFoundException;
import com.example.demo.java.user.controller.request.CreateUserRequest;
import com.example.demo.java.user.entity.User;
import com.example.demo.java.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public User get(String id) {
        return userRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public User create(CreateUserRequest request) {
        return userRepository.save(modelMapper.map(request, User.class));
    }

    public void followUser(String id, String userId) throws ItemNotFoundException {
        if(this.findById(userId).isEmpty()) {
            throw new ItemNotFoundException(String.format("The user %s doesn't exist.", userId));
        }

        var user = this.get(id);
        user.getFollowedUserIds().add(userId);
    }

}
