package org.hospital.hospitalservice.services;

import com.sun.source.tree.OpensTree;
import lombok.RequiredArgsConstructor;
import org.hospital.hospitalservice.entities.User;
import org.hospital.hospitalservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findUserByToken(String token) {
        return userRepository.findUserByToken(token);
    }

    public User updateUserChatID(User user, long chatID) {
        return userRepository.save(user.updateChatID(chatID));
    }

}
