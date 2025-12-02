package com.gymongo.repository;

import com.gymongo.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.gym.id = :gymId AND b.startTime = :startTime")
    long countByGymIdAndStartTime(@Param("gymId") Long gymId, @Param("startTime") LocalDateTime startTime);
}
