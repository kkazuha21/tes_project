package com.sharedlab.controller;

import com.sharedlab.model.entity.Device;
import com.sharedlab.model.enums.DeviceStatus;
import com.sharedlab.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@CrossOrigin
public class DeviceController {
    
    private final DeviceService deviceService;
    
    /**
     * 获取设备列表（支持筛选）
     */
    @GetMapping
    public ResponseEntity<List<Device>> getDevices(
            @RequestParam(required = false) DeviceStatus status,
            @RequestParam(required = false) Long categoryId
    ) {
        try {
            List<Device> devices = deviceService.getDevices(status, categoryId);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * 获取设备详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        return ResponseEntity.ok(device);
    }
    
    /**
     * 创建设备（仅管理员）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> createDevice(@RequestBody Device device) {
        Device createdDevice = deviceService.createDevice(device);
        return ResponseEntity.ok(createdDevice);
    }
    
    /**
     * 更新设备（仅管理员）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> updateDevice(
            @PathVariable Long id,
            @RequestBody Device device
    ) {
        Device updatedDevice = deviceService.updateDevice(id, device);
        return ResponseEntity.ok(updatedDevice);
    }
    
    /**
     * 删除设备（仅管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok().build();
    }
}
