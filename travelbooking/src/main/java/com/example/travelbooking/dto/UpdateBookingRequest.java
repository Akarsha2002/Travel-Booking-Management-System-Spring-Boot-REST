package com.example.travelbooking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateBookingRequest(

                @NotNull(message = "Room ID is required") Long roomId,

                @NotNull(message = "Check-in date is required") @JsonFormat(pattern = "dd-MM-yyyy") LocalDate checkInDate,

                @NotNull(message = "Check-out date is required") @JsonFormat(pattern = "dd-MM-yyyy") LocalDate checkOutDate

) {
}