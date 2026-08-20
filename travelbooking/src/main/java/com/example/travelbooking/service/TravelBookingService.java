package com.example.travelbooking.service;

import com.example.travelbooking.dto.*;
import com.example.travelbooking.entity.*;
import com.example.travelbooking.exception.*;
import com.example.travelbooking.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
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

    @Transactional(readOnly = true)
    public List<HotelResponse> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::toHotelResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public HotelResponse getHotelById(Long hotelId) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with ID: " + hotelId
                        )
                );
                
        return toHotelResponse(hotel);
    }

    public HotelResponse updateHotel(
        Long hotelId,
        UpdateHotelRequest request
) {

    Hotel hotel = hotelRepository
            .findById(hotelId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Hotel not found with ID: "
                                    + hotelId
                    )
            );

    hotel.setName(request.name().trim());
    hotel.setCity(request.city().trim());

    hotel = hotelRepository.save(hotel);

    return new HotelResponse(
            hotel.getId(),
            hotel.getName(),
            hotel.getCity()
    );
}

public void deleteHotel(Long hotelId) {

    Hotel hotel = hotelRepository
            .findById(hotelId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Hotel not found with ID: "
                                    + hotelId
                    )
            );

    boolean hasRooms = roomRepository.existsByHotel_Id(hotelId);

    if (hasRooms) {
        throw new BadRequestException(
                "Hotel cannot be deleted because rooms exist. "
                        + "Delete the rooms first."
        );
    }

    hotelRepository.delete(hotel);
}

    public RoomResponse addRoom(Long hotelId, AddRoomRequest request) {

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

    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::toRoomResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByHotelId(Long hotelId) {

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with ID: "     
                                        + hotelId
                        )
                );

        return roomRepository.findByHotel_Id(hotelId).stream()
                .map(this::toRoomResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long hotelId, Long roomId) {

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with ID: "
                                        + hotelId
                        )
                );

        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with ID: "
                                        + roomId
                        )
                );

        if (!room.getHotel()
                 .getId()
                 .equals(hotel.getId())) {

            throw new BadRequestException(
                    "Room does not belong to this hotel"
            );
        }

        return toRoomResponse(room);
        }

    public RoomResponse updateRoom(
        Long hotelId,
        Long roomId,
        UpdateRoomRequest request
    ) {

    Hotel hotel = hotelRepository
            .findById(hotelId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Hotel not found with ID: "
                                    + hotelId
                    )
            );

    Room room = roomRepository
            .findById(roomId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Room not found with ID: "
                                    + roomId
                    )
            );

    if (!room.getHotel()
            .getId()
            .equals(hotelId)) {

        throw new BadRequestException(
                "Room does not belong to this hotel"
        );
    }

    boolean duplicate =
            roomRepository
                    .existsByHotel_IdAndRoomNumberIgnoreCaseAndIdNot(
                            hotelId,
                            request.roomNumber().trim(),
                            roomId
                    );

    if (duplicate) {
        throw new BadRequestException(
                "Room number already exists in this hotel"
        );
    }

    room.setRoomNumber( request.roomNumber().trim());
    room.setRoomType(request.roomType());
    room.setPricePerNight(request.pricePerNight());

    room.setHotel(hotel);

    room = roomRepository.save(room);

    return toRoomResponse(room);
}

public void deleteRoom(Long hotelId, Long roomId) {

    Room room = roomRepository
            .findById(roomId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Room not found with ID: "
                                    + roomId
                    )
            );

    if (!room.getHotel()
            .getId()
            .equals(hotelId)) {

        throw new BadRequestException(
                "Room does not belong to this hotel"
        );
    }

    boolean hasBookings =
            bookingRepository.existsByRoom_Id(
                    roomId
            );

    if (hasBookings) {
        throw new BadRequestException(
                "Room cannot be deleted because bookings exist"
        );
    }

    roomRepository.delete(room);
}

@Transactional(readOnly = true)
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

    public BookingResponse updateBooking(
        Long bookingId,
        UpdateBookingRequest request
) {

    Booking booking =
            bookingRepository
                    .findById(bookingId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Booking not found with ID: "
                                            + bookingId
                            )
                    );

    validateDates(
            request.checkInDate(),
            request.checkOutDate()
    );

    Room room =
            roomRepository
                    .findById(request.roomId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Room not found with ID: "
                                            + request.roomId()
                            )
                    );

    if (booking.getStatus()
            == BookingStatus.CONFIRMED) {

        long overlap =
                bookingRepository
                        .countOverlappingBookingsExcludingBooking(
                                room.getId(),
                                bookingId,
                                BookingStatus.CONFIRMED,
                                request.checkInDate(),
                                request.checkOutDate()
                        );

        if (overlap > 0) {
            throw new BookingConflictException(
                    "Room is already booked for the selected dates"
            );
        }
    }

    long numberOfNights =
            ChronoUnit.DAYS.between(
                    request.checkInDate(),
                    request.checkOutDate()
            );

    BigDecimal totalAmount =
            room.getPricePerNight()
                    .multiply(
                            BigDecimal.valueOf(
                                    numberOfNights
                            )
                    );

    booking.setRoom(room);
    booking.setCheckInDate(request.checkInDate());
    booking.setCheckOutDate(request.checkOutDate());
    booking.setTotalAmount(totalAmount);

    booking = bookingRepository.save(booking);

    return toBookingResponse(booking);
}

public void deleteBooking(Long bookingId) {

    Booking booking =
            bookingRepository
                    .findById(bookingId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Booking not found with ID: "
                                            + bookingId
                            )
                    );

    bookingRepository.delete(booking);
}

@Transactional(readOnly = true)
public List<BookingResponse> getAllBookings() {

        return bookingRepository
                .findAll()
                .stream()
                .map(this::toBookingResponse)
                .toList();
    }

@Transactional(readOnly = true)
public BookingResponse getBookingById(Long bookingId) {

        Booking booking =
                bookingRepository
                        .findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found with ID: "
                                                + bookingId
                                )
                        );

        return toBookingResponse(booking);
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {

        if (!checkOut.isAfter(checkIn)) {
            throw new BadRequestException(
                    "Check-out date must be after check-in date"
            );
        }
    }

    private HotelResponse toHotelResponse(Hotel hotel) {

        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity()
        );
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