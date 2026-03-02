package com.sharedlab.repository;

import com.sharedlab.model.entity.Device;
import com.sharedlab.model.enums.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    List<Device> findByStatus(DeviceStatus status);
    
    List<Device> findByDeviceCategoryId(Long categoryId);
    
    List<Device> findByStatusAndDeviceCategoryId(DeviceStatus status, Long categoryId);
    
    Long countByDeviceCategoryId(Long categoryId);
}
