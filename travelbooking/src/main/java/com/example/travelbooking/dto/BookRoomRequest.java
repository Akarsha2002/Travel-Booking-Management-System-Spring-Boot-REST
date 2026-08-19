package com.example.travelbooking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record BookRoomRequest(

        @NotNull(message = "Room ID is required")
        Long roomId,

        @NotBlank(message = "Customer name is required")
        String customerName,

        @NotBlank(message = "Customer email is required")
        @Email(message = "Customer email must be valid")
        String customerEmail,

        @NotBlank(message = "Customer phone is required")
        String customerPhone,

        @NotNull(message = "Check-in date is required")
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate checkInDate,

        @NotNull(message = "Check-out date is required")
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate checkOutDate

) {
}