package com.sharedlab.controller;

import com.sharedlab.model.dto.ChangePasswordRequest;
import com.sharedlab.model.dto.ProfileUpdateRequest;
import com.sharedlab.model.dto.UserResponse;
import com.sharedlab.model.entity.User;
import com.sharedlab.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin
public class ProfileController {

    private final UserService userService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    /**
     * 当前登录用户修改自己的个人信息（username/email）
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        User user = userService.updateMyProfile(authentication.getName(), request);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    /**
     * 当前登录用户修改自己的密码（需要提供旧密码）。
     */
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> changeMyPassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changeMyPassword(authentication.getName(), request);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "密码修改成功");
        return ResponseEntity.ok(body);
    }
}
