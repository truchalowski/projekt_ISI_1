package org.projekt.isi.repository;

import org.projekt.isi.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByReservationDateBetween(LocalDateTime start, LocalDateTime end);

    List<Reservation> findByUserIdAndReservationDateBetween(Long employeeId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Reservation> findByEmployeeIdAndReservationDateBetween(Long employeeId, LocalDateTime startOfDay, LocalDateTime endOfDay);

}
