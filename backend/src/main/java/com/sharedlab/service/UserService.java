package com.sharedlab.service;

import com.sharedlab.exception.ResourceNotFoundException;
import com.sharedlab.model.dto.ChangePasswordRequest;
import com.sharedlab.model.dto.ProfileUpdateRequest;
import com.sharedlab.model.dto.UserRequest;
import com.sharedlab.model.entity.User;
import com.sharedlab.model.enums.UserRole;
import com.sharedlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 注册新用户
     */
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }
    
    /**
     * 根据用户名获取用户
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 根据邮箱获取用户
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // ========== 管理员用户管理功能 ==========
    
    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * 根据ID获取用户
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }
    
    /**
     * 根据角色筛选用户
     */
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * 创建用户
     */
    @Transactional
    public User createUser(UserRequest request) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("邮箱已被使用");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        
        return userRepository.save(user);
    }
    
    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long id, UserRequest request) {
        User user = getUserById(id);
        
        // 检查用户名是否被其他用户使用
        if (!user.getUsername().equals(request.getUsername())) {
            userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new IllegalArgumentException("用户名已存在");
                }
            });
            user.setUsername(request.getUsername());
        }
        
        // 检查邮箱是否被其他用户使用
        if (!user.getEmail().equals(request.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new IllegalArgumentException("邮箱已被使用");
                }
            });
            user.setEmail(request.getEmail());
        }
        
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        
        // 如果提供了新密码,则更新密码
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        
        return userRepository.save(user);
    }
    
    /**
     * 切换用户状态（启用/禁用）
     */
    @Transactional
    public User toggleUserStatus(Long id) {
        User user = getUserById(id);
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        return userRepository.save(user);
    }
    
    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    /**
     * 统计各角色用户数量
     */
    public long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    // ========== 个人信息自助修改功能 ==========

    /**
     * 当前登录用户修改自己的个人信息（仅允许修改 username/email）。
     */
    @Transactional
    public User updateMyProfile(String currentUsername, ProfileUpdateRequest request) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // 检查用户名是否被其他用户使用
        if (!user.getUsername().equals(request.getUsername())) {
            userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
                if (!u.getId().equals(user.getId())) {
                    throw new IllegalArgumentException("用户名已存在");
                }
            });
            user.setUsername(request.getUsername());
        }

        // 检查邮箱是否被其他用户使用
        if (!user.getEmail().equals(request.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(user.getId())) {
                    throw new IllegalArgumentException("邮箱已被使用");
                }
            });
            user.setEmail(request.getEmail());
        }

        return userRepository.save(user);
    }

    /**
     * 当前登录用户修改自己的密码（需要提供旧密码）。
     */
    @Transactional
    public void changeMyPassword(String currentUsername, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        boolean matches = passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash());
        if (!matches) {
            throw new IllegalArgumentException("旧密码不正确");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
