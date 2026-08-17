package com.example.travelbooking.dto;

import com.example.travelbooking.entity.RoomType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AddRoomRequest(

        @NotBlank(message = "Room number is required")
        String roomNumber,

        @NotNull(message = "Room type is required")
        RoomType roomType,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01")
        BigDecimal pricePerNight

) {
}