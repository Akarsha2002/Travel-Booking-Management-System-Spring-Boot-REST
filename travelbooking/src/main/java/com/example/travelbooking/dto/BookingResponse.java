package com.example.travelbooking.dto;

import com.example.travelbooking.entity.BookingStatus;
import com.example.travelbooking.entity.RoomType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingResponse(

                Long id,

                String customerName,
                String customerEmail,
                String customerPhone,

                Long hotelId,
                String hotelName,
                Long roomId,
                String roomNumber,
                RoomType roomType,

                @JsonFormat(pattern = "dd-MM-yyyy") LocalDate checkInDate,

                @JsonFormat(pattern = "dd-MM-yyyy") LocalDate checkOutDate,

                BigDecimal totalAmount,
                BookingStatus status) {
}