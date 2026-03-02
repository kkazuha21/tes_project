package com.sharedlab.controller;

import com.sharedlab.model.dto.JwtResponse;
import com.sharedlab.model.dto.UserLogin;
import com.sharedlab.model.entity.User;
import com.sharedlab.model.enums.UserRole;
import com.sharedlab.service.JwtService;
import com.sharedlab.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLogin request) {
        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            
            // 生成JWT token
            String token = jwtService.generateToken(request.getUsername());
            
            // 获取用户信息
            User user = userService.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            // 返回JWT响应
            JwtResponse response = new JwtResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "用户名或密码错误");
            return ResponseEntity.status(401).body(error);
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // 设置默认角色为学生
            if (user.getRole() == null) {
                user.setRole(UserRole.STUDENT);
            }
            
            User registeredUser = userService.registerUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "注册成功");
            response.put("userId", registeredUser.getId());
            response.put("username", registeredUser.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
