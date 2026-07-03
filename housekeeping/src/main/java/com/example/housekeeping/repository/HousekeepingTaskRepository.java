package com.example.housekeeping.repository;

import com.example.housekeeping.model.HousekeepingTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, Long> {
}
