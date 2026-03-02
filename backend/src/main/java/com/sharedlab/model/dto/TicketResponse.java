package com.sharedlab.model.dto;

import com.sharedlab.model.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String title;
    private String content;
    private String severity;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private Long userId;
    private String username;
    private Long deviceId;
    private String deviceName;

    public static TicketResponse fromEntity(Ticket t) {
        TicketResponse r = new TicketResponse();
        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setContent(t.getContent());
        r.setSeverity(t.getSeverity());
        r.setStatus(t.getStatus() == null ? null : t.getStatus().name());
        r.setCreatedAt(t.getCreatedAt());
        r.setClosedAt(t.getClosedAt());
        if (t.getUser() != null) {
            r.setUserId(t.getUser().getId());
            r.setUsername(t.getUser().getUsername());
        }
        if (t.getDevice() != null) {
            r.setDeviceId(t.getDevice().getId());
            r.setDeviceName(t.getDevice().getName());
        }
        return r;
    }
}