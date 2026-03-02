package com.sharedlab.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.sharedlab.model.entity.User;
import com.sharedlab.repository.UserRepository;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        User result = userService.getUserById(1L);
        assertEquals("admin", result.getUsername());
    }
}
