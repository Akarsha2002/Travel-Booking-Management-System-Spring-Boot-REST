package com.example.travelbooking.dto;

import com.example.travelbooking.entity.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateRoomRequest(

        @NotBlank(message = "Room number is required")
        String roomNumber,

        @NotNull(message = "Room type is required")
        RoomType roomType,

        @NotNull(message = "Price is required")
        @DecimalMin(
                value = "0.01",
                message = "Price must be greater than zero"
        )
        BigDecimal pricePerNight

) {
}