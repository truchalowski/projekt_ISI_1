package org.projekt.isi.dto;

import java.time.LocalDateTime;

public class ReservationDTO {
    private LocalDateTime reservationDate;
    private Long userId;
    private Long serviceId; // Zmiana na identyfikator usługi

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDateTime getReservationDate(LocalDateTime reservationDate) {
        return reservationDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }


}
