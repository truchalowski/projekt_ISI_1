package org.projekt.isi.controller;

import org.projekt.isi.dto.ReservationDTO;
import org.projekt.isi.entity.Reservation;
import org.projekt.isi.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsForUser(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReservations(@RequestBody ReservationDTO reservationDTO) {
        Reservation reservation = reservationService.createReservation(reservationDTO);
        return ResponseEntity.ok(reservation);
    }
}