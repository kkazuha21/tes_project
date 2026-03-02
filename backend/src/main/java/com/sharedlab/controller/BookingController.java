package com.sharedlab.controller;

import com.sharedlab.exception.BookingConflictException;
import com.sharedlab.model.dto.BookingRequest;
import com.sharedlab.model.dto.BookingResponse;
import com.sharedlab.model.entity.Booking;
import com.sharedlab.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {
    
    private final BookingService bookingService;
    
    /**
     * 创建预约
     */
    @PostMapping
    public ResponseEntity<?> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            Booking booking = bookingService.createBooking(request, username);
            return ResponseEntity.ok(BookingResponse.fromEntity(booking));
            
        } catch (BookingConflictException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(409).body(error);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * 获取当前用户的预约记录
     */
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        String username = authentication.getName();
        List<Booking> bookings = bookingService.getUserBookings(username);
        List<BookingResponse> responses = bookings.stream()
                .map(BookingResponse::fromEntity)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 获取设备的预约记录（可选时间范围过滤）
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getDeviceBookings(
            @RequestParam Long deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        List<Booking> bookings;
        if (startTime != null && endTime != null) {
            bookings = bookingService.getDeviceBookingsInRange(deviceId, startTime, endTime);
        } else {
            bookings = bookingService.getDeviceBookings(deviceId);
        }
        List<BookingResponse> responses = bookings.stream()
                .map(BookingResponse::fromEntity)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 取消预约
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            Booking booking = bookingService.cancelBooking(id, username);
            return ResponseEntity.ok(BookingResponse.fromEntity(booking));
            
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * 获取所有预约（仅管理员）
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')") public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        List<BookingResponse> responses = bookings.stream()
                .map(BookingResponse::fromEntity)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
