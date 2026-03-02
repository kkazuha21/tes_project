package com.sharedlab.service;

import com.sharedlab.exception.ResourceNotFoundException;
import com.sharedlab.model.entity.Device;
import com.sharedlab.model.enums.DeviceStatus;
import com.sharedlab.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    
    /**
     * 获取所有设备
     */
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }
    
    /**
     * 根据条件筛选设备
     */
    public List<Device> getDevices(DeviceStatus status, Long categoryId) {
        if (status != null && categoryId != null) {
            return deviceRepository.findByStatusAndDeviceCategoryId(status, categoryId);
        } else if (status != null) {
            return deviceRepository.findByStatus(status);
        } else if (categoryId != null) {
            return deviceRepository.findByDeviceCategoryId(categoryId);
        } else {
            return deviceRepository.findAll();
        }
    }
    
    /**
     * 根据ID获取设备
     */
    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("设备不存在"));
    }
    
    /**
     * 创建设备
     */
    public Device createDevice(Device device) {
        return deviceRepository.save(device);
    }
    
    /**
     * 更新设备
     */
    public Device updateDevice(Long id, Device deviceDetails) {
        Device device = getDeviceById(id);
        
        device.setName(deviceDetails.getName());
        device.setStatus(deviceDetails.getStatus());
        device.setLocation(deviceDetails.getLocation());
        device.setDesc(deviceDetails.getDesc());
        device.setDeviceCategory(deviceDetails.getDeviceCategory());
        
        return deviceRepository.save(device);
    }
    
    /**
     * 删除设备
     */
    public void deleteDevice(Long id) {
        Device device = getDeviceById(id);
        deviceRepository.delete(device);
    }
}
