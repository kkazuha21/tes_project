package com.sharedlab.model.dto;

import com.sharedlab.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private UserRole role;
    
    public JwtResponse(String token, Long userId, String username, String email, UserRole role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
