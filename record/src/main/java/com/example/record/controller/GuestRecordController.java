package com.example.record.controller;

import com.example.record.model.GuestProfile;
import com.example.record.service.GuestRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestRecordController {

    private final GuestRecordService guestRecordService;

    public GuestRecordController(GuestRecordService guestRecordService) {
        this.guestRecordService = guestRecordService;
    }

    @GetMapping({"", "/"})
    public List<GuestProfile> getAllGuests() {
        return guestRecordService.getAllGuests();
    }

    @GetMapping({"/{id}", "/{id}/"})
    public ResponseEntity<GuestProfile> getGuest(@PathVariable Long id) {
        return guestRecordService.getGuest(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GuestProfile> createGuest(@RequestBody GuestProfile guest) {
        GuestProfile created = guestRecordService.createGuest(guest);
        return ResponseEntity.created(URI.create("/api/guests/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuestProfile> updateGuest(@PathVariable Long id, @RequestBody GuestProfile guest) {
        return guestRecordService.updateGuest(id, guest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
