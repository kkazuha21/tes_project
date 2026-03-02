package com.sharedlab.service;

import com.sharedlab.exception.BookingConflictException;
import com.sharedlab.exception.ResourceNotFoundException;
import com.sharedlab.model.dto.BookingRequest;
import com.sharedlab.model.entity.Booking;
import com.sharedlab.model.entity.Device;
import com.sharedlab.model.entity.User;
import com.sharedlab.model.enums.BookingStatus;
import com.sharedlab.repository.BookingRepository;
import com.sharedlab.repository.DeviceRepository;
import com.sharedlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    
    /**
     * 创建预约（包含冲突检测逻辑）
     */
    @Transactional
    public Booking createBooking(BookingRequest request, String username) {
        // 获取当前用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        
        // 获取设备
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("设备不存在"));
        
        // 验证时间逻辑
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("结束时间必须晚于开始时间");
        }
        
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("开始时间不能早于当前时间");
        }
        
        // 核心：冲突检测
        // 先查询出所有冲突的预约，以便提供详细信息
        List<Booking> conflictingBookings = bookingRepository.findDeviceBookingsInTimeRange(
                request.getDeviceId(),
                request.getStartTime(),
                request.getEndTime()
        ).stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .collect(java.util.stream.Collectors.toList());
        
        if (!conflictingBookings.isEmpty()) {
            // 构建详细的冲突信息
            Booking firstConflict = conflictingBookings.get(0);
            String message = String.format(
                "该时间段设备已被预约。冲突预约: %s 至 %s (预约人: %s)",
                firstConflict.getStartTime(),
                firstConflict.getEndTime(),
                firstConflict.getUser().getUsername()
            );
            throw new BookingConflictException(message);
        }
        
        // 创建预约
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setDevice(device);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus(BookingStatus.CONFIRMED);
        
        return bookingRepository.save(booking);
    }
    
    /**
     * 获取用户的所有预约
     */
    public List<Booking> getUserBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        return bookingRepository.findByUserId(user.getId());
    }
    
    /**
     * 获取设备的所有预约
     */
    public List<Booking> getDeviceBookings(Long deviceId) {
        return bookingRepository.findByDeviceId(deviceId);
    }
    
    /**
     * 获取设备在指定时间范围内的预约（用于前端日历显示）
     */
    public List<Booking> getDeviceBookingsInRange(Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingRepository.findDeviceBookingsInTimeRange(deviceId, startTime, endTime);
    }
    
    /**
     * 取消预约
     */
    @Transactional
    public Booking cancelBooking(Long bookingId, String username) {
        Booking booking = bookingRepository.findByIdWithUserAndDevice(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("预约不存在"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // 权限检查：只有预约者本人或管理员可以取消
        if (!booking.getUser().getId().equals(user.getId()) &&
            !user.getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("没有权限取消此预约");
        }        // 只有未开始的预约才能取消
        if (booking.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("已开始或已结束的预约无法取消");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }
    
    /**
     * 获取预约详情
     */
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("预约不存在"));
    }
    
    /**
     * 获取所有预约（管理员）
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAllWithUserAndDevice();
    }
}
