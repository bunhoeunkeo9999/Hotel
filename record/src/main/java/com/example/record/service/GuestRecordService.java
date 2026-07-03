package com.example.record.service;

import com.example.record.model.GuestProfile;
import com.example.record.repository.GuestProfileRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GuestRecordService {

    private final GuestProfileRepository guestProfileRepository;

    public GuestRecordService(GuestProfileRepository guestProfileRepository) {
        this.guestProfileRepository = guestProfileRepository;
    }

    @PostConstruct
    public void init() {
        if (guestProfileRepository.count() == 0) {
            guestProfileRepository.save(new GuestProfile(null, "John Doe", LocalDateTime.now().minusDays(1)));
            guestProfileRepository.save(new GuestProfile(null, "Jane Smith", LocalDateTime.now().minusHours(12)));
        }
    }

    public List<GuestProfile> getAllGuests() {
        return guestProfileRepository.findAll();
    }

    public Optional<GuestProfile> getGuest(Long id) {
        return guestProfileRepository.findById(id);
    }

    public GuestProfile createGuest(GuestProfile guest) {
        if (guest.getCheckIn() == null) {
            guest.setCheckIn(LocalDateTime.now());
        }
        return guestProfileRepository.save(guest);
    }

    public Optional<GuestProfile> updateGuest(Long id, GuestProfile update) {
        return guestProfileRepository.findById(id).map(existing -> {
            existing.setName(update.getName());
            if (update.getCheckIn() != null) {
                existing.setCheckIn(update.getCheckIn());
            }
            return guestProfileRepository.save(existing);
        });
    }
}
