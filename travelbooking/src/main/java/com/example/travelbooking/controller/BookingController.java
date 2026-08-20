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

    // Book a room
    @PostMapping
    public ResponseEntity<BookingResponse> bookRoom(

            @Valid
            @RequestBody
            BookRoomRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.bookRoom(request));
    }

    // Update an existing booking
    @PutMapping("/{bookingId}")
    public BookingResponse updateBooking(

            @PathVariable
            Long bookingId,

            @Valid
            @RequestBody
            UpdateBookingRequest request
    ) {

        return service.updateBooking(bookingId, request);
    }

    // Cancel a booking
    @PatchMapping("/{bookingId}/cancel")
    public BookingResponse cancelBooking(@PathVariable Long bookingId) {

        return service.cancelBooking(bookingId);
    }

    // Delete a booking
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId) {

        service.deleteBooking(bookingId);

        return ResponseEntity.noContent().build();
    }

    // Fetch all bookings
    @GetMapping
    public List<BookingResponse> getAllBookings() {

        return service.getAllBookings();
    }

    // Fetch a booking by ID
    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(@PathVariable Long bookingId) {
    
        return service.getBookingById(bookingId);
    }
    
}