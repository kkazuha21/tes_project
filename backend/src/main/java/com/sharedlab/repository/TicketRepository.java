package com.sharedlab.repository;

import com.sharedlab.model.entity.Ticket;
import com.sharedlab.model.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t JOIN FETCH t.user JOIN FETCH t.device WHERE t.id = :id")
    Optional<Ticket> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT t FROM Ticket t JOIN FETCH t.user JOIN FETCH t.device WHERE t.user.id = :userId")
    List<Ticket> findByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Ticket t JOIN FETCH t.user JOIN FETCH t.device WHERE t.device.id = :deviceId")
    List<Ticket> findByDeviceId(@Param("deviceId") Long deviceId);

    List<Ticket> findByStatus(TicketStatus status);

    @Query("SELECT t FROM Ticket t JOIN FETCH t.user JOIN FETCH t.device ORDER BY t.createdAt DESC")
    List<Ticket> findAllByOrderByCreatedAtDesc();
}
