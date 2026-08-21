package com.example.travelbooking.dto;

import com.example.travelbooking.entity.RoomType;

import java.math.BigDecimal;

public record RoomResponse(
                Long id,
                Long hotelId,
                String hotelName,
                String roomNumber,
                RoomType roomType,
                BigDecimal pricePerNight) {
}