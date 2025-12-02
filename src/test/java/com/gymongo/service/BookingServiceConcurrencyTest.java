package com.gymongo.service;

import com.gymongo.entity.Booking;
import com.gymongo.entity.Gym;
import com.gymongo.entity.User;
import com.gymongo.repository.BookingRepository;
import com.gymongo.repository.GymRepository;
import com.gymongo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookingServiceConcurrencyTest {

    @Test
    public void concurrentBookings_dontExceedCapacity() throws InterruptedException, ExecutionException {
        Long gymId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        // prepare a Gym with capacity 2
        Gym gym = new Gym();
        gym.setId(gymId);
        gym.setCapacity(2);

        // mock GymRepository
        GymRepository gymRepo = mock(GymRepository.class);
        when(gymRepo.findById(eq(gymId))).thenReturn(java.util.Optional.of(gym));

        // prepare users repository - simple mock that returns users by id
        UserRepository userRepo = mock(UserRepository.class);
        when(userRepo.findById(any())).thenAnswer((Answer<java.util.Optional<User>>) invocation -> {
            Object arg = invocation.getArgument(0);
            Long id = (arg instanceof Long) ? (Long) arg : Long.valueOf(arg.toString());
            User u = new User();
            u.setId(id);
            u.setUsername("u" + id);
            return java.util.Optional.of(u);
        });

        // booking repository - track saved bookings in a thread-safe set and enforce capacity on save
        BookingRepository bookingRepo = mock(BookingRepository.class);
        Set<Long> bookedUsers = Collections.synchronizedSet(new HashSet<>());

        when(bookingRepo.countByGymIdAndStartTime(eq(gymId), eq(start))).thenAnswer(invocation -> (long) bookedUsers.size());

        when(bookingRepo.save(any(Booking.class))).thenAnswer((Answer<Booking>) invocation -> {
            Booking b = invocation.getArgument(0);
            synchronized (bookedUsers) {
                if (bookedUsers.size() >= gym.getCapacity()) {
                    throw new IllegalStateException("Gym is fully booked at this time");
                }
                bookedUsers.add(b.getUser().getId());
            }
            return b;
        });

        BookingService svc = new BookingService(bookingRepo, userRepo, gymRepo);

        int attempts = 6; // more than capacity to exercise contention
        ExecutorService ex = Executors.newFixedThreadPool(attempts);
        try {
            CompletionService<Boolean> cs = new ExecutorCompletionService<>(ex);
            for (int i = 0; i < attempts; i++) {
                final long uid = i + 1L;
                cs.submit(() -> {
                    try {
                        svc.createBooking(uid, gymId, start);
                        return true;
                    } catch (IllegalStateException ise) {
                        return false;
                    }
                });
            }

            int success = 0;
            for (int i = 0; i < attempts; i++) {
                Future<Boolean> f = cs.take();
                if (f.get()) success++;
            }

            // verify we never exceeded capacity
            assertThat(success).isLessThanOrEqualTo(gym.getCapacity());
            assertThat(bookedUsers.size()).isEqualTo(success);
        } finally {
            ex.shutdownNow();
        }
    }
}
