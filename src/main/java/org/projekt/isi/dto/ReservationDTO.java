package org.projekt.isi.dto;

import java.time.LocalDateTime;

public class ReservationDTO {
    private LocalDateTime dateTime;
    private Long userId;
    private String service; // Dodane pole service

    // Gettery i settery
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}