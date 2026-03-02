package com.sharedlab.model.dto;

import com.sharedlab.model.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long userId;
    private String username;
    private Long deviceId;
    private String deviceName;
    private String deviceLocation;
    private LocalDateTime createdAt;

    public static BookingResponse fromEntity(Booking b) {
        BookingResponse r = new BookingResponse();
        r.setId(b.getId());
        r.setStartTime(b.getStartTime());
        r.setEndTime(b.getEndTime());
        r.setStatus(b.getStatus() == null ? null : b.getStatus().name());
        r.setCreatedAt(b.getCreatedAt());
        if (b.getUser() != null) {
            r.setUserId(b.getUser().getId());
            r.setUsername(b.getUser().getUsername());
        }
        if (b.getDevice() != null) {
            r.setDeviceId(b.getDevice().getId());
            r.setDeviceName(b.getDevice().getName());
            r.setDeviceLocation(b.getDevice().getLocation());
        }
        return r;
    }
}