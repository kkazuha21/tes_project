package com.sharedlab.service;

import com.sharedlab.exception.ResourceNotFoundException;
import com.sharedlab.model.entity.Device;
import com.sharedlab.model.entity.DeviceCategory;
import com.sharedlab.model.enums.DeviceStatus;
import com.sharedlab.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DeviceService 单元测试
 * 测试设备管理服务的核心业务逻辑
 * 
 * @author 同学乙
 */
@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    private Device testDevice;
    private DeviceCategory testCategory;

    @BeforeEach
    void setUp() {
        // 初始化测试分类
        testCategory = new DeviceCategory();
        testCategory.setId(1L);
        testCategory.setName("实验仪器");

        // 初始化测试设备
        testDevice = new Device();
        testDevice.setId(1L);
        testDevice.setName("光学显微镜");
        testDevice.setStatus(DeviceStatus.AVAILABLE);
        testDevice.setLocation("实验室A101");
        testDevice.setDesc("用于生物实验观察");
        testDevice.setDeviceCategory(testCategory);
    }

    @Test
    void testGetAllDevices_Success() {
        // Arrange
        Device device2 = new Device();
        device2.setId(2L);
        device2.setName("电子天平");
        device2.setStatus(DeviceStatus.AVAILABLE);
        
        List<Device> expectedDevices = Arrays.asList(testDevice, device2);
        when(deviceRepository.findAll()).thenReturn(expectedDevices);

        // Act
        List<Device> result = deviceService.getAllDevices();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("光学显微镜", result.get(0).getName());
        assertEquals("电子天平", result.get(1).getName());
        verify(deviceRepository, times(1)).findAll();
    }

    @Test
    void testGetDevices_ByStatus() {
        // Arrange
        List<Device> expectedDevices = Arrays.asList(testDevice);
        when(deviceRepository.findByStatus(DeviceStatus.AVAILABLE))
                .thenReturn(expectedDevices);

        // Act
        List<Device> result = deviceService.getDevices(DeviceStatus.AVAILABLE, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(DeviceStatus.AVAILABLE, result.get(0).getStatus());
        verify(deviceRepository, times(1)).findByStatus(DeviceStatus.AVAILABLE);
    }

    @Test
    void testGetDevices_ByCategoryId() {
        // Arrange
        List<Device> expectedDevices = Arrays.asList(testDevice);
        when(deviceRepository.findByDeviceCategoryId(1L)).thenReturn(expectedDevices);

        // Act
        List<Device> result = deviceService.getDevices(null, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("实验仪器", result.get(0).getDeviceCategory().getName());
        verify(deviceRepository, times(1)).findByDeviceCategoryId(1L);
    }

    @Test
    void testGetDevices_ByStatusAndCategory() {
        // Arrange
        List<Device> expectedDevices = Arrays.asList(testDevice);
        when(deviceRepository.findByStatusAndDeviceCategoryId(DeviceStatus.AVAILABLE, 1L))
                .thenReturn(expectedDevices);

        // Act
        List<Device> result = deviceService.getDevices(DeviceStatus.AVAILABLE, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deviceRepository, times(1))
                .findByStatusAndDeviceCategoryId(DeviceStatus.AVAILABLE, 1L);
    }

    @Test
    void testGetDeviceById_Success() {
        // Arrange
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        // Act
        Device result = deviceService.getDeviceById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("光学显微镜", result.getName());
        verify(deviceRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDeviceById_NotFound() {
        // Arrange
        when(deviceRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deviceService.getDeviceById(999L)
        );
        assertEquals("设备不存在", exception.getMessage());
        verify(deviceRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateDevice_Success() {
        // Arrange
        Device newDevice = new Device();
        newDevice.setName("新设备");
        newDevice.setStatus(DeviceStatus.AVAILABLE);
        
        when(deviceRepository.save(any(Device.class))).thenReturn(newDevice);

        // Act
        Device result = deviceService.createDevice(newDevice);

        // Assert
        assertNotNull(result);
        assertEquals("新设备", result.getName());
        verify(deviceRepository, times(1)).save(newDevice);
    }

    @Test
    void testUpdateDevice_Success() {
        // Arrange
        Device updatedDetails = new Device();
        updatedDetails.setName("更新后的显微镜");
        updatedDetails.setStatus(DeviceStatus.MAINTENANCE);
        updatedDetails.setLocation("实验室B202");
        updatedDetails.setDesc("已升级镜头");
        updatedDetails.setDeviceCategory(testCategory);
        
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // Act
        Device result = deviceService.updateDevice(1L, updatedDetails);

        // Assert
        assertNotNull(result);
        assertEquals("更新后的显微镜", result.getName());
        assertEquals(DeviceStatus.MAINTENANCE, result.getStatus());
        assertEquals("实验室B202", result.getLocation());
        verify(deviceRepository, times(1)).findById(1L);
        verify(deviceRepository, times(1)).save(testDevice);
    }

    @Test
    void testUpdateDevice_NotFound() {
        // Arrange
        Device updatedDetails = new Device();
        updatedDetails.setName("更新设备");
        
        when(deviceRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
                () -> deviceService.updateDevice(999L, updatedDetails));
        verify(deviceRepository, times(1)).findById(999L);
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void testDeleteDevice_Success() {
        // Arrange
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));
        doNothing().when(deviceRepository).delete(testDevice);

        // Act
        deviceService.deleteDevice(1L);

        // Assert
        verify(deviceRepository, times(1)).findById(1L);
        verify(deviceRepository, times(1)).delete(testDevice);
    }

    @Test
    void testDeleteDevice_NotFound() {
        // Arrange
        when(deviceRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> deviceService.deleteDevice(999L));
        verify(deviceRepository, times(1)).findById(999L);
        verify(deviceRepository, never()).delete(any(Device.class));
    }

    @Test
    void testGetDevices_NoFilter() {
        // Arrange
        List<Device> expectedDevices = Arrays.asList(testDevice);
        when(deviceRepository.findAll()).thenReturn(expectedDevices);

        // Act
        List<Device> result = deviceService.getDevices(null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deviceRepository, times(1)).findAll();
    }

    @Test
    void testUpdateDevice_AllFields() {
        // Arrange
        DeviceCategory newCategory = new DeviceCategory();
        newCategory.setId(2L);
        newCategory.setName("新分类");
        
        Device updatedDetails = new Device();
        updatedDetails.setName("全新设备名称");
        updatedDetails.setStatus(DeviceStatus.IN_USE);
        updatedDetails.setLocation("新位置");
        updatedDetails.setDesc("新描述");
        updatedDetails.setDeviceCategory(newCategory);
        
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        // Act
        Device result = deviceService.updateDevice(1L, updatedDetails);

        // Assert
        assertNotNull(result);
        assertEquals("全新设备名称", result.getName());
        assertEquals(DeviceStatus.IN_USE, result.getStatus());
        assertEquals("新位置", result.getLocation());
        assertEquals("新描述", result.getDesc());
        assertEquals(2L, result.getDeviceCategory().getId());
    }
}
