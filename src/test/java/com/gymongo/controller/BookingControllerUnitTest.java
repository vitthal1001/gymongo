package com.gymongo.controller;

import com.gymongo.entity.Booking;
import com.gymongo.entity.User;
import com.gymongo.repository.BookingRepository;
import com.gymongo.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookingControllerUnitTest {

    @Test
    public void createBooking_success() {
        BookingRepository bookingRepo = mock(BookingRepository.class);
        BookingService bookingService = mock(BookingService.class);
        com.gymongo.repository.UserRepository userRepo = mock(com.gymongo.repository.UserRepository.class);

        BookingController controller = new BookingController(bookingRepo, bookingService, userRepo);

        User bob = new User();
        bob.setId(10L);
        bob.setUsername("bob");

        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bob));

        Booking created = new Booking();
        created.setId(100L);
    when(bookingService.createBooking(eq(10L), eq(1L), any(LocalDateTime.class))).thenReturn(created);

        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn("bob");

        Map<String, String> body = Map.of(
                "gymId", "1",
                "startTime", LocalDateTime.now().plusDays(1).toString()
        );

        ResponseEntity<?> resp = controller.create(ud, body);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isEqualTo(created);
    }

    @Test
    public void createBooking_whenFull_returnsBadRequest() {
        BookingRepository bookingRepo = mock(BookingRepository.class);
        BookingService bookingService = mock(BookingService.class);
        com.gymongo.repository.UserRepository userRepo = mock(com.gymongo.repository.UserRepository.class);

        BookingController controller = new BookingController(bookingRepo, bookingService, userRepo);

        User alice = new User();
        alice.setId(11L);
        alice.setUsername("alice");
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));

    when(bookingService.createBooking(eq(11L), eq(1L), any(LocalDateTime.class)))
        .thenThrow(new IllegalStateException("Gym is fully booked at this time"));

        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn("alice");

        Map<String, String> body = Map.of(
                "gymId", "1",
                "startTime", LocalDateTime.now().plusDays(1).toString()
        );

        ResponseEntity<?> resp = controller.create(ud, body);
        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
        assertThat(resp.getBody()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> err = (Map<String, String>) resp.getBody();
        assertThat(err.get("error")).contains("Gym is fully booked");
    }
}
