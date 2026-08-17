package com.example.travelbooking.service;

import com.example.travelbooking.dto.*;
import com.example.travelbooking.entity.*;
import com.example.travelbooking.exception.*;
import com.example.travelbooking.repository.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TravelBookingService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public TravelBookingService(
            HotelRepository hotelRepository,
            RoomRepository roomRepository,
            CustomerRepository customerRepository,
            BookingRepository bookingRepository
    ) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
    }

    public HotelResponse addHotel(AddHotelRequest request) {

        Hotel hotel = new Hotel(request.name(), request.city());
        hotel = hotelRepository.save(hotel);

        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity()
        );
    }

    public RoomResponse addRoom(Long hotelId, AddRoomRequest request
) {

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found"
                        )
                );

        Room room = new Room(
                request.roomNumber(),
                request.roomType(),
                request.pricePerNight(),
                hotel
        );

        room = roomRepository.save(room);

        return toRoomResponse(room);
    }

    public List<RoomResponse> getAvailableRooms(Long hotelId, LocalDate checkIn, LocalDate checkOut) {

        validateDates(checkIn, checkOut);

        return roomRepository
                .findByHotel_Id(hotelId)
                .stream()
                .filter(room ->
                        bookingRepository
                                .countOverlappingBookings(
                                        room.getId(),
                                        BookingStatus.CONFIRMED,
                                        checkIn,
                                        checkOut
                                ) == 0
                )
                .map(this::toRoomResponse)
                .toList();
    }

    public BookingResponse bookRoom(BookRoomRequest request) {

        validateDates(
                request.checkInDate(),
                request.checkOutDate()
        );

        Room room = roomRepository
                .findById(request.roomId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found"
                        )
                );

        long overlap =
                bookingRepository
                        .countOverlappingBookings(
                                room.getId(),
                                BookingStatus.CONFIRMED,
                                request.checkInDate(),
                                request.checkOutDate()
                        );

        if (overlap > 0) {
            throw new BookingConflictException(
                    "Room is already booked"
            );
        }

        Customer customer =
                customerRepository
                        .findByEmailIgnoreCase(
                                request.customerEmail()
                        )
                        .orElseGet(() ->
                                new Customer(
                                        request.customerName(),
                                        request.customerEmail(),
                                        request.customerPhone()
                                )
                        );

        customer = customerRepository.save(customer);

        long nights =
                ChronoUnit.DAYS.between(
                        request.checkInDate(),
                        request.checkOutDate()
                );

        BigDecimal total =
                room.getPricePerNight()
                        .multiply(
                                BigDecimal.valueOf(nights)
                        );

        Booking booking = new Booking(
                customer,
                room,
                request.checkInDate(),
                request.checkOutDate(),
                total,
                BookingStatus.CONFIRMED
        );

        booking = bookingRepository.save(booking);

        return toBookingResponse(booking);
    }

    public BookingResponse cancelBooking(Long bookingId) {

        Booking booking =
                bookingRepository
                        .findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found"
                                )
                        );

        booking.setStatus(BookingStatus.CANCELLED);

        return toBookingResponse(bookingRepository.save(booking));
    }

    public List<BookingResponse> getAllBookings() {

        return bookingRepository
                .findAll()
                .stream()
                .map(this::toBookingResponse)
                .toList();
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {

        if (!checkOut.isAfter(checkIn)) {
            throw new BadRequestException(
                    "Check-out date must be after check-in date"
            );
        }
    }

    private RoomResponse toRoomResponse(Room room) {

        return new RoomResponse(
                room.getId(),
                room.getHotel().getId(),
                room.getHotel().getName(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getPricePerNight()
        );
    }

    private BookingResponse toBookingResponse(Booking booking) {

        return new BookingResponse(
                booking.getId(),
                booking.getCustomer().getName(),
                booking.getCustomer().getEmail(),
                booking.getCustomer().getPhone(),
                booking.getRoom().getHotel().getId(),
                booking.getRoom().getHotel().getName(),
                booking.getRoom().getId(),
                booking.getRoom().getRoomNumber(),
                booking.getRoom().getRoomType(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getTotalAmount(),
                booking.getStatus()
        );
    }
}