package com.sharedlab.model.dto;

import com.sharedlab.model.entity.DeviceCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long deviceCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static CategoryResponse fromEntity(DeviceCategory category, Long deviceCount) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setDeviceCount(deviceCount);
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }
}
