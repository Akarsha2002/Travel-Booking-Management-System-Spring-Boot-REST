package com.example.travelbooking.dto;

import jakarta.validation.constraints.NotBlank;

public record AddHotelRequest(

                @NotBlank(message = "Hotel name is required") String name,

                @NotBlank(message = "City is required") String city

) {
}