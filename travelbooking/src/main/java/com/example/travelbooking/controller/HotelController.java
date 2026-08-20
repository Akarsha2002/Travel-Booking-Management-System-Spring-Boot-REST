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
    // Add a new hotel
    public ResponseEntity<HotelResponse> addHotel(
            @Valid @RequestBody
            AddHotelRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addHotel(request));
    }

    @GetMapping
    // Fetch all hotels
    public List<HotelResponse> getAllHotels() {
        return service.getAllHotels();
    }

    // Fetch a specific hotel by ID
    @GetMapping("/{hotelId}")
    public HotelResponse getHotelById(@PathVariable Long hotelId) {
        return service.getHotelById(hotelId);
    }

    // Update an existing hotel
    @PutMapping("/{hotelId}")
    public HotelResponse updateHotel(

            @PathVariable
            Long hotelId,

            @Valid
            @RequestBody
            UpdateHotelRequest request
    ) {

        return service.updateHotel(hotelId, request);
    }

    // Delete an existing hotel
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId) {

        service.deleteHotel(hotelId);

        return ResponseEntity.noContent().build();
    }

    // Add Room to a Hotel
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

    // Fetch all rooms
    @GetMapping("/rooms")
    public List<RoomResponse> getAllRooms() {
        return service.getAllRooms();
    }

    // Fetch all rooms for a specific hotel
    @GetMapping("/{hotelId}/rooms")
    public List<RoomResponse> getRoomsByHotelId(@PathVariable Long hotelId) {
        return service.getRoomsByHotelId(hotelId);
    }

    // Fetch a specific room in a hotel by ID
    @GetMapping("/{hotelId}/rooms/{roomId}")
    public RoomResponse getRoomById(

            @PathVariable
            Long hotelId,

            @PathVariable
            Long roomId
    ) {

        return service.getRoomById(hotelId, roomId);
    }

    // Update an existing room in a hotel
    @PutMapping("/{hotelId}/rooms/{roomId}")
    public RoomResponse updateRoom(

            @PathVariable
            Long hotelId,

            @PathVariable
            Long roomId,

            @Valid
            @RequestBody
            UpdateRoomRequest request
    ) {

        return service.updateRoom(hotelId, roomId, request);
    }

    // Delete a room from a hotel
    @DeleteMapping("/{hotelId}/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(

            @PathVariable
            Long hotelId,

            @PathVariable
            Long roomId
    ) {

        service.deleteRoom(hotelId, roomId);

        return ResponseEntity.noContent().build();
    }

    // Fetch available rooms for a hotel within a specified date range
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