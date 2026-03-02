package com.sharedlab.controller;

import com.sharedlab.model.dto.TicketSubmit;
import com.sharedlab.model.dto.TicketResponse;
import com.sharedlab.model.entity.Ticket;
import com.sharedlab.model.enums.TicketStatus;
import com.sharedlab.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin
public class TicketController {
    
    private final TicketService ticketService;
    
    /**
     * 提交报修
     */
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketSubmit request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Ticket ticket = ticketService.createTicket(request, username);
        return ResponseEntity.ok(TicketResponse.fromEntity(ticket));
    }
    
    /**
     * 获取当前用户的报修单
     */
    @GetMapping("/my")
    public ResponseEntity<List<TicketResponse>> getMyTickets(Authentication authentication) {
        String username = authentication.getName();
        List<Ticket> tickets = ticketService.getUserTickets(username);
        List<TicketResponse> responses = tickets.stream()
                .map(TicketResponse::fromEntity)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 获取所有报修单（仅管理员）
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')") public ResponseEntity<List<TicketResponse>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        List<TicketResponse> responses = tickets.stream()
                .map(TicketResponse::fromEntity)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 获取报修单详
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(TicketResponse.fromEntity(ticket));
    }
    
    /**
     * 更新报修单状态(仅管理员)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Transactional
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status
    ) {
        Ticket ticket = ticketService.updateTicketStatus(id, status);
        return ResponseEntity.ok(TicketResponse.fromEntity(ticket));
    }
}
