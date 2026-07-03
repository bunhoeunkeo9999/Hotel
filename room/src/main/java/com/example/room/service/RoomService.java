package com.example.room.service;

import com.example.room.model.Room;
import com.example.room.repository.RoomRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @PostConstruct
    public void init() {
        if (roomRepository.count() == 0) {
            roomRepository.save(new Room(null, "101", "Standard", BigDecimal.valueOf(120.00), "Vacant", ""));
            roomRepository.save(new Room(null, "102", "Deluxe", BigDecimal.valueOf(180.00), "Occupied", "John Doe"));
            roomRepository.save(new Room(null, "201", "Suite", BigDecimal.valueOf(250.00), "Vacant", ""));
        }
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Optional<Room> updateRoom(Long id, Room update) {
        return roomRepository.findById(id).map(existing -> {
            existing.setNumber(update.getNumber());
            existing.setType(update.getType());
            existing.setPrice(update.getPrice());
            existing.setStatus(update.getStatus());
            if (update.getGuestName() != null) {
                existing.setGuestName(update.getGuestName());
            }
            return roomRepository.save(existing);
        });
    }

    public Optional<Room> updateStatus(Long id, String status) {
        return roomRepository.findById(id).map(existing -> {
            existing.setStatus(status);
            return roomRepository.save(existing);
        });
    }
}
