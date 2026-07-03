package com.example.housekeeping.model;

import jakarta.persistence.*;

@Entity
@Table(name = "housekeeping_tasks")
public class HousekeepingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private String status;

    @Column(name = "assigned_staff")
    private String assignedStaff;

    @Column(length = 1024)
    private String notes;

    public HousekeepingTask() {
    }

    public HousekeepingTask(Long id, String roomNumber, String status, String assignedStaff, String notes) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.status = status;
        this.assignedStaff = assignedStaff;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedStaff() {
        return assignedStaff;
    }

    public void setAssignedStaff(String assignedStaff) {
        this.assignedStaff = assignedStaff;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
