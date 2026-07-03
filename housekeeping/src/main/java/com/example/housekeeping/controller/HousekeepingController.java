package com.example.housekeeping.controller;

import com.example.housekeeping.model.HousekeepingTask;
import com.example.housekeeping.service.HousekeepingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/housekeeping")
public class HousekeepingController {

    private final HousekeepingService housekeepingService;

    public HousekeepingController(HousekeepingService housekeepingService) {
        this.housekeepingService = housekeepingService;
    }

    @GetMapping("/tasks")
    public List<HousekeepingTask> getTasks() {
        return housekeepingService.getAllTasks();
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<HousekeepingTask> getTask(@PathVariable Long id) {
        return housekeepingService.getTask(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/tasks")
    public ResponseEntity<HousekeepingTask> createTask(@RequestBody HousekeepingTask task) {
        HousekeepingTask created = housekeepingService.createTask(task);
        return ResponseEntity.created(URI.create("/api/housekeeping/tasks/" + created.getId())).body(created);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<HousekeepingTask> updateTask(@PathVariable Long id, @RequestBody HousekeepingTask task) {
        return housekeepingService.updateTask(id, task)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
