package com.example.travelbooking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record BookRoomRequest(

        @NotNull
        Long roomId,

        @NotBlank
        String customerName,

        @NotBlank
        @Email
        String customerEmail,

        @NotBlank
        String customerPhone,

        @NotNull
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate checkInDate,

        @NotNull
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate checkOutDate

) {
}