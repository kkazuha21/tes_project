package com.sharedlab.repository;

import com.sharedlab.model.entity.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Long> {
    
    List<MaintenanceLog> findByTicketId(Long ticketId);
}
