package com.example.travelbooking.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateHotelRequest(

        @NotBlank(message = "Hotel name is required")
        String name,

        @NotBlank(message = "City is required")
        String city

) {
}