package com.sharedlab.service;

import com.sharedlab.model.dto.CategoryResponse;
import com.sharedlab.model.entity.DeviceCategory;
import com.sharedlab.repository.DeviceCategoryRepository;
import com.sharedlab.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private DeviceCategoryRepository categoryRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> {
                    Long deviceCount = deviceRepository.countByDeviceCategoryId(category.getId());
                    return CategoryResponse.fromEntity(category, deviceCount);
                })
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        DeviceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        Long deviceCount = deviceRepository.countByDeviceCategoryId(id);
        return CategoryResponse.fromEntity(category, deviceCount);
    }

    @Transactional
    public DeviceCategory createCategory(DeviceCategory category) {
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new RuntimeException("分类名称已存在");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public DeviceCategory updateCategory(Long id, DeviceCategory categoryDetails) {
        DeviceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        // 检查名称是否与其他分类重复
        categoryRepository.findByName(categoryDetails.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new RuntimeException("分类名称已存在");
                    }
                });
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        DeviceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        // 检查是否有关联的设备
        Long deviceCount = deviceRepository.countByDeviceCategoryId(id);
        if (deviceCount > 0) {
            throw new RuntimeException("该分类下还有 " + deviceCount + " 台设备，无法删除");
        }
        
        categoryRepository.delete(category);
    }
}
