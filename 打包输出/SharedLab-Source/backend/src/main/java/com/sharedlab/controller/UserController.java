package com.sharedlab.controller;

import com.sharedlab.model.dto.UserRequest;
import com.sharedlab.model.dto.UserResponse;
import com.sharedlab.model.entity.User;
import com.sharedlab.model.enums.UserRole;
import com.sharedlab.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取所有用户
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) UserRole role
    ) {
        List<User> users;
        if (role != null) {
            users = userService.getUsersByRole(role);
        } else {
            users = userService.getAllUsers();
        }
        
        List<UserResponse> response = users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取用户统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getAllUsers().size());
        stats.put("adminCount", userService.countUsersByRole(UserRole.ADMIN));
        stats.put("teacherCount", userService.countUsersByRole(UserRole.TEACHER));
        stats.put("studentCount", userService.countUsersByRole(UserRole.STUDENT));
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("创建用户时密码不能为空");
        }
        
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.fromEntity(user));
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request
    ) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
    
    /**
     * 切换用户状态（启用/禁用）
     */
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable Long id) {
        User user = userService.toggleUserStatus(id);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
