package com.example.travelbooking.repository;

import com.example.travelbooking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotel_Id(Long hotelId);

    boolean existsByHotel_Id(Long hotelId);

    boolean existsByHotel_IdAndRoomNumberIgnoreCaseAndIdNot(
            Long hotelId,
            String roomNumber,
            Long roomId
    );
}