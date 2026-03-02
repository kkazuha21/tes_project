package com.sharedlab.service;

import com.sharedlab.model.entity.Booking;
import com.sharedlab.model.entity.Device;
import com.sharedlab.model.enums.BookingStatus;
import com.sharedlab.model.enums.DeviceStatus;
import com.sharedlab.repository.BookingRepository;
import com.sharedlab.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceStatusScheduler {

    private final BookingRepository bookingRepository;
    private final DeviceRepository deviceRepository;

    /**
     * 每分钟检查一次设备状态
     * 根据预约时间自动更新设备状态
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟的第0秒执行
    @Transactional
    public void updateDeviceStatus() {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. 查找所有已确认的预约
        List<Booking> confirmedBookings = bookingRepository.findByStatus(BookingStatus.CONFIRMED);
        
        for (Booking booking : confirmedBookings) {
            Device device = booking.getDevice();
            
            // 2. 检查预约是否已开始但尚未结束
            if (now.isAfter(booking.getStartTime()) && now.isBefore(booking.getEndTime())) {
                // 预约进行中，设备应该是"使用中"状态
                if (device.getStatus() != DeviceStatus.IN_USE) {
                    device.setStatus(DeviceStatus.IN_USE);
                    deviceRepository.save(device);
                    log.info("设备 {} 状态已更新为使用中 (预约ID: {})", device.getName(), booking.getId());
                }
            }
            // 3. 检查预约是否已结束
            else if (now.isAfter(booking.getEndTime())) {
                // 预约已结束，检查该设备是否还有其他进行中的预约
                boolean hasOtherActiveBooking = bookingRepository.existsByDeviceAndStatusAndStartTimeBeforeAndEndTimeAfter(
                    device,
                    BookingStatus.CONFIRMED,
                    now,
                    now
                );
                
                // 如果没有其他进行中的预约，将设备状态改为可用
                if (!hasOtherActiveBooking && device.getStatus() == DeviceStatus.IN_USE) {
                    device.setStatus(DeviceStatus.AVAILABLE);
                    deviceRepository.save(device);
                    log.info("设备 {} 状态已更新为可用 (预约ID: {} 已结束)", device.getName(), booking.getId());
                }
            }
        }
    }
}
