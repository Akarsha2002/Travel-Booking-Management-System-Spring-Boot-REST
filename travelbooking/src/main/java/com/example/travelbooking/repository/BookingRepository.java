package com.example.travelbooking.repository;

import com.example.travelbooking.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            select count(b)
            from Booking b
            where b.room.id = :roomId
              and b.status = :status
              and :checkIn < b.checkOutDate
              and :checkOut > b.checkInDate
            """)
    long countOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("status") BookingStatus status,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

     @Query("""
            select count(b)
            from Booking b
            where b.room.id = :roomId
              and b.id <> :bookingId
              and b.status = :status
              and :checkIn < b.checkOutDate
              and :checkOut > b.checkInDate
            """)
    long countOverlappingBookingsExcludingBooking(

            @Param("roomId")
            Long roomId,

            @Param("bookingId")
            Long bookingId,

            @Param("status")
            BookingStatus status,

            @Param("checkIn")
            LocalDate checkIn,

            @Param("checkOut")
            LocalDate checkOut
    );

    boolean existsByRoom_Id(Long roomId);
}