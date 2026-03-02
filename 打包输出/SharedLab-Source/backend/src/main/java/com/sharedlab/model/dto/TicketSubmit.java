package com.sharedlab.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketSubmit {
    
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    private String content;
    
    private String severity;
}
