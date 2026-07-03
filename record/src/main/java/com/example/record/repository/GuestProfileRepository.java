package com.example.record.repository;

import com.example.record.model.GuestProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestProfileRepository extends JpaRepository<GuestProfile, Long> {
}
