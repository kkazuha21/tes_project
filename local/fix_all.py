import re

# 1. 修复BookingService
backup = open(r'C:\SharedLab\backend\src\main\java\com\sharedlab\service\BookingService.java.backup', encoding='utf-8').read()
booking = backup.replace(
    'import com.sharedlab.model.dto.BookingRequest;', 
    'import com.sharedlab.model.dto.BookingRequest;\nimport com.sharedlab.model.dto.BookingResponse;\nimport java.util.stream.Collectors;'
).replace(
    'public Booking createBooking(BookingRequest request, String username)',
    'public BookingResponse createBooking(BookingRequest request, String username)'
).replace(
    'return bookingRepository.save(booking);',
    'Booking saved = bookingRepository.save(booking);\n        return BookingResponse.fromEntity(saved);'
).replace(
    'public List<Booking> getUserBookings(String username)',
    '@Transactional(readOnly = true)\n    public List<BookingResponse> getUserBookings(String username)'
).replace(
    'return bookingRepository.findByUserId(user.getId());',
    'List<Booking> bookings = bookingRepository.findByUserId(user.getId());\n        return bookings.stream().map(BookingResponse::fromEntity).collect(Collectors.toList());'
)
open(r'C:\SharedLab\backend\src\main\java\com\sharedlab\service\BookingService.java', 'w', encoding='utf-8').write(booking)
print('OK: BookingService')

# 2. 创建正确的TicketService
ticket = '''package com.sharedlab.service;

import com.sharedlab.exception.ResourceNotFoundException;
import com.sharedlab.model.dto.TicketResponse;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Transactional
    public TicketResponse createTicket(TicketSubmit submit, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(\"用户不存在\"));

        Device device = deviceRepository.findById(submit.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException(\"设备不存在\"));

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setDevice(device);
        ticket.setTitle(submit.getTitle());
        ticket.setContent(submit.getContent());
        ticket.setSeverity(submit.getSeverity());
        ticket.setStatus(TicketStatus.OPEN);

        Ticket saved = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getUserTickets(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(\"用户不存在\"));
        List<Ticket> tickets = ticketRepository.findByUserId(user.getId());
        return tickets.stream()
                .map(TicketResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<Ticket> getDeviceTickets(Long deviceId) {
        return ticketRepository.findByDeviceId(deviceId);
    }

    @Transactional
    public Ticket closeTicket(Long ticketId, String username) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(\"工单不存在\"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(\"用户不存在\"));

        if (!user.getRole().name().equals(\"ADMIN\") && !user.getRole().name().equals(\"TEACHER\")) {
            throw new AccessDeniedException(\"只有管理员和教师可以关闭工单\");
        }

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setClosedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(\"工单不存在\"));
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}
'''
open(r'C:\SharedLab\backend\src\main\java\com\sharedlab\service\TicketService.java', 'w', encoding='utf-8').write(ticket)
print('OK: TicketService')
print('? 完成!')
