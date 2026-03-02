package com.sharedlab.repository;

import com.sharedlab.model.entity.DeviceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceCategoryRepository extends JpaRepository<DeviceCategory, Long> {
    
    Optional<DeviceCategory> findByName(String name);
}
