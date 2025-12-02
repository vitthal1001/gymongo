package com.gymongo.controller;

import com.gymongo.entity.Booking;
import com.gymongo.repository.BookingRepository;
import com.gymongo.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final com.gymongo.repository.UserRepository userRepository;

    public BookingController(BookingRepository bookingRepository, BookingService bookingService, com.gymongo.repository.UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Booking> list() {
        return bookingRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal UserDetails ud, @RequestBody Map<String, String> body) {
        Long gymId = Long.valueOf(body.get("gymId"));
        LocalDateTime startTime = LocalDateTime.parse(body.get("startTime"));
        Long userId = userRepository.findByUsername(ud.getUsername()).map(u -> u.getId()).orElse(null);
        if (userId == null) return ResponseEntity.badRequest().body(Map.of("error", "user_not_found"));
        try {
            Booking b = bookingService.createBooking(userId, gymId, startTime);
            return ResponseEntity.ok(b);
        } catch (IllegalStateException ise) {
            return ResponseEntity.badRequest().body(Map.of("error", ise.getMessage()));
        }
    }
}
