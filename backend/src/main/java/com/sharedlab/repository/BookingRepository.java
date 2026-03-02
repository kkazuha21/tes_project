package com.sharedlab.repository;

import com.sharedlab.model.entity.Booking;
import com.sharedlab.model.entity.Device;
import com.sharedlab.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.device WHERE b.user.id = :userId")
    List<Booking> findByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.device WHERE b.device.id = :deviceId")
    List<Booking> findByDeviceId(@Param("deviceId") Long deviceId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.device")
    List<Booking> findAllWithUserAndDevice();

    @Query("SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.device WHERE b.id = :id")
    java.util.Optional<Booking> findByIdWithUserAndDevice(@Param("id") Long id);

    List<Booking> findByStatus(BookingStatus status);
    
    /**
     * 用于冲突检测：检查指定设备在给定时间段内是否存在未取消的预约
     * 时间段重叠的条件：新预约的开始时间 < 已有预约的结束时间 AND 新预约的结束时间 > 已有预约的开始时间
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
           "FROM Booking b " +
           "WHERE b.device.id = :deviceId " +
           "AND b.status != :status " +
           "AND b.startTime < :endTime " +
           "AND b.endTime > :startTime")
    boolean existsByDeviceIdAndStatusNotAndStartTimeBeforeAndEndTimeAfter(
        @Param("deviceId") Long deviceId,
        @Param("status") BookingStatus status,
        @Param("endTime") LocalDateTime endTime,
        @Param("startTime") LocalDateTime startTime
    );
    
    /**
     * 获取设备在指定时间段内的所有预约（用于前端日历显示）
     */
    @Query("SELECT b FROM Booking b " +
           "WHERE b.device.id = :deviceId " +
           "AND b.status != 'CANCELLED' " +
           "AND b.startTime < :endTime " +
           "AND b.endTime > :startTime")
    List<Booking> findDeviceBookingsInTimeRange(
        @Param("deviceId") Long deviceId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 检查设备在指定时间段内是否有进行中的预约（用于定时任务）
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
           "FROM Booking b " +
           "WHERE b.device = :device " +
           "AND b.status = :status " +
           "AND b.startTime < :endTime " +
           "AND b.endTime > :startTime")
    boolean existsByDeviceAndStatusAndStartTimeBeforeAndEndTimeAfter(
        @Param("device") Device device,
        @Param("status") BookingStatus status,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
