package com.gymongo.service;

import com.gymongo.entity.Booking;
import com.gymongo.entity.Gym;
import com.gymongo.entity.User;
import com.gymongo.repository.BookingRepository;
import com.gymongo.repository.GymRepository;
import com.gymongo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// no unused imports

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    BookingRepository bookingRepository;
    UserRepository userRepository;
    GymRepository gymRepository;
    BookingService bookingService;

    @BeforeEach
    void setup() {
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        gymRepository = mock(GymRepository.class);
        bookingService = new BookingService(bookingRepository, userRepository, gymRepository);
    }

    @Test
    void createBooking_happyPath_savesBooking() {
        Long userId = 1L;
        Long gymId = 2L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        User user = new User(); user.setId(userId);
        Gym gym = new Gym(); gym.setId(gymId); gym.setCapacity(5);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gymRepository.findById(gymId)).thenReturn(Optional.of(gym));
        when(bookingRepository.countByGymIdAndStartTime(gymId, start)).thenReturn(2L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createBooking(userId, gymId, start);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getGym()).isEqualTo(gym);
        assertThat(result.getStartTime()).isEqualTo(start);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_capacityExceeded_throws() {
        Long userId = 1L;
        Long gymId = 2L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        User user = new User(); user.setId(userId);
        Gym gym = new Gym(); gym.setId(gymId); gym.setCapacity(3);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gymRepository.findById(gymId)).thenReturn(Optional.of(gym));
        when(bookingRepository.countByGymIdAndStartTime(gymId, start)).thenReturn(3L);

        assertThatThrownBy(() -> bookingService.createBooking(userId, gymId, start))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("fully booked");

        verify(bookingRepository, never()).save(any());
    }
}
