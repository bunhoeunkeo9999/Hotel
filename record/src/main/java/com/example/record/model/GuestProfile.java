package com.example.record.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "guest_profiles")
public class GuestProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "check_in", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime checkIn;

    public GuestProfile() {
    }

    public GuestProfile(Long id, String name, LocalDateTime checkIn) {
        this.id = id;
        this.name = name;
        this.checkIn = checkIn != null ? checkIn : LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }
}
