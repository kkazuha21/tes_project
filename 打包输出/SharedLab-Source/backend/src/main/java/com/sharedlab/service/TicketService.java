package com.sharedlab.service;

import com.sharedlab.exception.ResourceNotFoundException;
import com.sharedlab.model.dto.TicketSubmit;
import com.sharedlab.model.entity.Device;
import com.sharedlab.model.entity.Ticket;
import com.sharedlab.model.entity.User;
import com.sharedlab.model.enums.TicketStatus;
import com.sharedlab.repository.DeviceRepository;
import com.sharedlab.repository.TicketRepository;
import com.sharedlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Transactional
    public Ticket createTicket(TicketSubmit submit, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Device device = deviceRepository.findById(submit.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setDevice(device);
        ticket.setTitle(submit.getTitle());
        ticket.setContent(submit.getContent());
        ticket.setSeverity(submit.getSeverity());
        ticket.setStatus(TicketStatus.SUBMITTED);

        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getUserTickets(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ticketRepository.findByUserId(user.getId());
    }

    public List<Ticket> getDeviceTickets(Long deviceId) {
        return ticketRepository.findByDeviceId(deviceId);
    }

    @Transactional
    public Ticket closeTicket(Long ticketId, String username) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRole().name().equals("ADMIN") && !user.getRole().name().equals("TEACHER")) {
            throw new AccessDeniedException("Only admin or teacher can close ticket");
        }

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setClosedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Ticket updateTicketStatus(Long id, TicketStatus status) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        ticket.setStatus(status);
        if (status == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }
        return ticketRepository.save(ticket);
    }
}