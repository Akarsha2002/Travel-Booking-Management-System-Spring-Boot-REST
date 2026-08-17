package com.example.travelbooking.controller;

import com.example.travelbooking.dto.*;
import com.example.travelbooking.service.TravelBookingService;

import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final TravelBookingService service;

    public BookingController(
            TravelBookingService service
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> bookRoom(

            @Valid
            @RequestBody
            BookRoomRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.bookRoom(request)
                );
    }

    @PatchMapping("/{bookingId}/cancel")
    public BookingResponse cancelBooking(

            @PathVariable
            Long bookingId
    ) {

        return service.cancelBooking(
                bookingId
        );
    }

    @GetMapping
    public List<BookingResponse>
    getAllBookings() {

        return service.getAllBookings();
    }
}