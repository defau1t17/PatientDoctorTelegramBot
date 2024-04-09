package org.hospital.hospitalservice.services;

import lombok.RequiredArgsConstructor;
import org.hospital.hospitalservice.entities.User;
import org.hospital.hospitalservice.repositories.UserRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @CachePut(value = "userByToken", key = "T(Object)", unless = "#result == null")
    public Optional<User> findUserByToken(String token) {
        return userRepository.findUserByToken(token);
    }

    @CachePut(value = "userByChatID", key = "T(Object)", unless = "#result == null")
    public User updateUserChatID(User user, long chatID) {
        return userRepository.save(user.updateChatID(chatID));
    }

}
