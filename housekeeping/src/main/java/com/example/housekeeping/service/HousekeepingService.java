package com.example.housekeeping.service;

import com.example.housekeeping.model.HousekeepingTask;
import com.example.housekeeping.repository.HousekeepingTaskRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class HousekeepingService {

    private final HousekeepingTaskRepository taskRepository;

    public HousekeepingService(HousekeepingTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void init() {
        if (taskRepository.count() == 0) {
            taskRepository.save(new HousekeepingTask(null, "101", "Dirty", "Alice", "Clean before guest arrival"));
            taskRepository.save(new HousekeepingTask(null, "102", "Cleaning", "Bob", "Turn down service"));
            taskRepository.save(new HousekeepingTask(null, "201", "Clean", "", "Ready for check-in"));
        }
    }

    public List<HousekeepingTask> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<HousekeepingTask> getTask(Long id) {
        return taskRepository.findById(id);
    }

    public HousekeepingTask createTask(HousekeepingTask task) {
        return taskRepository.save(task);
    }

    public Optional<HousekeepingTask> updateTask(Long id, HousekeepingTask update) {
        return taskRepository.findById(id).map(existing -> {
            existing.setRoomNumber(update.getRoomNumber());
            existing.setStatus(update.getStatus());
            existing.setAssignedStaff(update.getAssignedStaff());
            existing.setNotes(update.getNotes());
            return taskRepository.save(existing);
        });
    }
}
