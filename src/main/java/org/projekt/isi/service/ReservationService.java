package org.projekt.isi.service;

import org.projekt.isi.dto.ReservationDTO;
import org.projekt.isi.entity.Reservation;
import org.projekt.isi.entity.User;
import org.projekt.isi.repository.ReservationRepository;
import org.projekt.isi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ReservationDTO> getReservationsForUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setDateTime(reservation.getDateTime());
        dto.setUserId(reservation.getUser().getId());
        dto.setService(reservation.getService());
        return dto;
    }

    public Reservation createReservation(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        reservation.setDateTime(reservationDTO.getDateTime());
        reservation.setService(reservationDTO.getService());
        reservation.setStatus("PENDING");

        User user = userRepository.findById(reservationDTO.getUserId()).orElse(null);
        reservation.setUser(user);

        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Long reservationId, ReservationDTO reservationDTO) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation != null) {
            reservation.setDateTime(reservationDTO.getDateTime());
            reservation.setService(reservationDTO.getService());
            return reservationRepository.save(reservation);
        }
        return null;
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        // Zmiana statusu rezerwacji na "CANCELLED"
        reservation.setStatus("CANCELLED");

        // Zapisanie zmienionej rezerwacji w bazie danych
        reservationRepository.save(reservation);
    }

    public Reservation confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus("CONFIRMED"); // Zakładając, że masz enum ReservationStatus

        return reservationRepository.save(reservation);
    }

}
