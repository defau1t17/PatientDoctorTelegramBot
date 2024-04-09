package org.hospital.hospitalservice.services;

import org.hospital.hospitalservice.entities.User;
import org.hospital.hospitalservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    private UserService userServiceUnderTest;

    private User user;

    @BeforeEach
    void setUp() {
        userServiceUnderTest = new UserService(mockUserRepository);
        user = spy();
    }

    @Test
    void testFindUserByToken() {
        when(mockUserRepository.findUserByToken(anyString())).thenReturn(Optional.of(user));
        final Optional<User> result = userServiceUnderTest.findUserByToken(anyString());
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testFindUserByToken_UserRepositoryReturnsAbsent() {
        when(mockUserRepository.findUserByToken(anyString())).thenReturn(Optional.empty());
        final Optional<User> result = userServiceUnderTest.findUserByToken(anyString());
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testUpdateUserChatID() {
        when(mockUserRepository.save(any(User.class))).thenReturn(user);
        final User result = userServiceUnderTest.updateUserChatID(spy(), 0L);
        assertEquals(user, result);
    }
}
