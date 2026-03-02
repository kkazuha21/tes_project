package com.sharedlab.model.entity;

import com.sharedlab.model.enums.DeviceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Device 实体类单元测试
 * 测试设备实体的字段验证和基本功能
 * 
 * @author 同学乙
 */
class DeviceTest {

    private Device device;
    private DeviceCategory category;

    @BeforeEach
    void setUp() {
        device = new Device();
        category = new DeviceCategory();
        category.setId(1L);
        category.setName("测试分类");
    }

    @Test
    void testDeviceCreation() {
        // Act
        device.setId(1L);
        device.setName("测试设备");
        device.setStatus(DeviceStatus.AVAILABLE);
        device.setLocation("实验室A101");
        device.setDesc("这是一个测试设备");
        device.setDeviceCategory(category);

        // Assert
        assertEquals(1L, device.getId());
        assertEquals("测试设备", device.getName());
        assertEquals(DeviceStatus.AVAILABLE, device.getStatus());
        assertEquals("实验室A101", device.getLocation());
        assertEquals("这是一个测试设备", device.getDesc());
        assertNotNull(device.getDeviceCategory());
        assertEquals(1L, device.getDeviceCategory().getId());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange & Act
        device.setId(2L);
        device.setName("光学显微镜");
        device.setStatus(DeviceStatus.IN_USE);
        device.setLocation("B202");
        device.setDesc("用于生物观察");

        // Assert
        assertEquals(2L, device.getId());
        assertEquals("光学显微镜", device.getName());
        assertEquals(DeviceStatus.IN_USE, device.getStatus());
        assertEquals("B202", device.getLocation());
        assertEquals("用于生物观察", device.getDesc());
    }

    @Test
    void testDeviceStatusEnum() {
        // Test all possible device statuses
        device.setStatus(DeviceStatus.AVAILABLE);
        assertEquals(DeviceStatus.AVAILABLE, device.getStatus());

        device.setStatus(DeviceStatus.IN_USE);
        assertEquals(DeviceStatus.IN_USE, device.getStatus());

        device.setStatus(DeviceStatus.MAINTENANCE);
        assertEquals(DeviceStatus.MAINTENANCE, device.getStatus());
    }

    @Test
    void testDeviceCategory() {
        // Arrange
        DeviceCategory newCategory = new DeviceCategory();
        newCategory.setId(2L);
        newCategory.setName("新分类");

        // Act
        device.setDeviceCategory(newCategory);

        // Assert
        assertNotNull(device.getDeviceCategory());
        assertEquals(2L, device.getDeviceCategory().getId());
        assertEquals("新分类", device.getDeviceCategory().getName());
    }

    @Test
    void testDeviceCategoryNull() {
        // Act
        device.setDeviceCategory(null);

        // Assert
        assertNull(device.getDeviceCategory());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        Device emptyDevice = new Device();

        // Assert
        assertNotNull(emptyDevice);
        assertNull(emptyDevice.getId());
        assertNull(emptyDevice.getName());
        // Device has default status AVAILABLE
        assertEquals(DeviceStatus.AVAILABLE, emptyDevice.getStatus());
        assertNull(emptyDevice.getLocation());
        assertNull(emptyDevice.getDesc());
        assertNull(emptyDevice.getDeviceCategory());
    }

    @Test
    void testAllArgsConstructor() {
        // Act
        Device fullDevice = new Device(
                1L, 
                "显微镜", 
                DeviceStatus.AVAILABLE, 
                "A101", 
                "测试描述",
                category,
                null,
                null
        );

        // Assert
        assertEquals(1L, fullDevice.getId());
        assertEquals("显微镜", fullDevice.getName());
        assertEquals(DeviceStatus.AVAILABLE, fullDevice.getStatus());
        assertEquals("A101", fullDevice.getLocation());
        assertEquals("测试描述", fullDevice.getDesc());
        assertEquals(category, fullDevice.getDeviceCategory());
    }

    @Test
    void testUpdateDeviceInformation() {
        // Arrange
        device.setId(1L);
        device.setName("旧名称");
        device.setStatus(DeviceStatus.AVAILABLE);
        device.setLocation("旧位置");
        device.setDesc("旧描述");

        // Act - 模拟更新操作
        device.setName("新名称");
        device.setStatus(DeviceStatus.MAINTENANCE);
        device.setLocation("新位置");
        device.setDesc("新描述");

        // Assert
        assertEquals(1L, device.getId());
        assertEquals("新名称", device.getName());
        assertEquals(DeviceStatus.MAINTENANCE, device.getStatus());
        assertEquals("新位置", device.getLocation());
        assertEquals("新描述", device.getDesc());
    }

    @Test
    void testDeviceStatusTransitions() {
        // Test valid status transitions
        device.setStatus(DeviceStatus.AVAILABLE);
        assertEquals(DeviceStatus.AVAILABLE, device.getStatus());

        // Available -> In Use
        device.setStatus(DeviceStatus.IN_USE);
        assertEquals(DeviceStatus.IN_USE, device.getStatus());

        // In Use -> Maintenance
        device.setStatus(DeviceStatus.MAINTENANCE);
        assertEquals(DeviceStatus.MAINTENANCE, device.getStatus());

        // Maintenance -> Available
        device.setStatus(DeviceStatus.AVAILABLE);
        assertEquals(DeviceStatus.AVAILABLE, device.getStatus());
    }

    @Test
    void testDeviceNameValidation() {
        // Test empty name
        device.setName("");
        assertEquals("", device.getName());

        // Test null name
        device.setName(null);
        assertNull(device.getName());

        // Test normal name
        device.setName("正常设备名称");
        assertEquals("正常设备名称", device.getName());
    }

    @Test
    void testDeviceLocationValidation() {
        // Test various location formats
        device.setLocation("A101");
        assertEquals("A101", device.getLocation());

        device.setLocation("实验楼3层305室");
        assertEquals("实验楼3层305室", device.getLocation());

        device.setLocation("");
        assertEquals("", device.getLocation());
    }

    @Test
    void testDeviceDescriptionLength() {
        // Test long description
        String longDesc = "这是一个非常详细的设备描述，包含了大量的信息来说明该设备的用途、特性和使用注意事项。".repeat(10);
        device.setDesc(longDesc);
        assertEquals(longDesc, device.getDesc());

        // Test short description
        device.setDesc("简短描述");
        assertEquals("简短描述", device.getDesc());
    }

    @Test
    void testDeviceEquality() {
        // Create two devices with same ID
        Device device1 = new Device();
        device1.setId(1L);
        device1.setName("设备1");

        Device device2 = new Device();
        device2.setId(1L);
        device2.setName("设备2");

        // In real scenario, entities with same ID should be equal
        assertEquals(device1.getId(), device2.getId());
    }

    @Test
    void testDeviceCategoryRelationship() {
        // Test bidirectional relationship
        DeviceCategory category1 = new DeviceCategory();
        category1.setId(1L);
        category1.setName("分类1");

        device.setDeviceCategory(category1);

        assertNotNull(device.getDeviceCategory());
        assertEquals("分类1", device.getDeviceCategory().getName());
    }
}
