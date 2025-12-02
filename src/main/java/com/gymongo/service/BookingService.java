package com.gymongo.service;

import com.gymongo.entity.Booking;
import com.gymongo.entity.Gym;
import com.gymongo.entity.User;
import com.gymongo.repository.BookingRepository;
import com.gymongo.repository.GymRepository;
import com.gymongo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final GymRepository gymRepository;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, GymRepository gymRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.gymRepository = gymRepository;
    }

    public Booking createBooking(Long userId, Long gymId, LocalDateTime startTime) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Gym gym = gymRepository.findById(gymId).orElseThrow(() -> new IllegalArgumentException("Gym not found"));

        long existingCount = bookingRepository.countByGymIdAndStartTime(gymId, startTime);
        if (existingCount >= gym.getCapacity()) {
            throw new IllegalStateException("Gym is fully booked at this time");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setGym(gym);
        booking.setStartTime(startTime);
        return bookingRepository.save(booking);
    }
}
