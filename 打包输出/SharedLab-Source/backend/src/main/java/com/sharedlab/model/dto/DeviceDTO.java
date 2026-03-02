package com.sharedlab.model.dto;

import com.sharedlab.model.entity.Device;
import com.sharedlab.model.enums.DeviceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDTO {
    private Long id;
    private String name;
    private String location;
    private String status;
    private String description;
    private String categoryName;
    private Long categoryId;

    public static DeviceDTO fromEntity(Device device) {
        if (device == null) return null;
        return new DeviceDTO(
            device.getId(),
            device.getName(),
            device.getLocation(),
            device.getStatus() != null ? device.getStatus().name() : null,
            device.getDesc(),
            device.getDeviceCategory() != null ? device.getDeviceCategory().getName() : null,
            device.getDeviceCategory() != null ? device.getDeviceCategory().getId() : null
        );
    }
}
