package org.projekt.isi.service;

import org.projekt.isi.dto.ReservationDTO;
import org.projekt.isi.entity.Reservation;
import org.projekt.isi.entity.User;
import org.projekt.isi.repository.ReservationRepository;
import org.projekt.isi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        dto.setService(reservation.getService()); // Ustawienie pola service w DTO
        return dto;
    }

    public Reservation createReservation(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        reservation.setDateTime(reservationDTO.getDateTime());
        reservation.setService(reservationDTO.getService()); // Ustawienie pola service

        // Szukamy użytkownika o identyfikatorze podanym w DTO
        User user = userRepository.findById(reservationDTO.getUserId()).orElse(null);

        // Przypisujemy użytkownika do rezerwacji
        reservation.setUser(user);

        // Zapisujemy rezerwację
        return reservationRepository.save(reservation);
    }
}