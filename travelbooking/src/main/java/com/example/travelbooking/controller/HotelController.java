package com.example.travelbooking.controller;

import com.example.travelbooking.dto.*;
import com.example.travelbooking.service.TravelBookingService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final TravelBookingService service;

    public HotelController(TravelBookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<HotelResponse> addHotel(
            @Valid @RequestBody
            AddHotelRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addHotel(request));
    }

    @PostMapping("/{hotelId}/rooms")
    public ResponseEntity<RoomResponse> addRoom(

            @PathVariable Long hotelId,

            @Valid
            @RequestBody
            AddRoomRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addRoom(hotelId, request));
    }

    @GetMapping("/{hotelId}/rooms/available")
    public List<RoomResponse> availableRooms(

            @PathVariable Long hotelId,

            @RequestParam
            @DateTimeFormat(pattern = "dd-MM-yyyy")
            LocalDate checkIn,

            @RequestParam
            @DateTimeFormat(pattern = "dd-MM-yyyy")
            LocalDate checkOut
    ) {

        return service.getAvailableRooms(hotelId, checkIn, checkOut);
    }
}