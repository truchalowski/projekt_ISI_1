package org.projekt.isi.service;

import org.projekt.isi.dto.ReservationDTO;
import org.projekt.isi.entity.Reservation;
import org.projekt.isi.entity.Services;
import org.projekt.isi.entity.User;
import org.projekt.isi.exceptions.ReservationConflictException;
import org.projekt.isi.repository.ReservationRepository;
import org.projekt.isi.repository.ServicesRepository;
import org.projekt.isi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServicesRepository serviceRepository;


    public List<ReservationDTO> getReservationsForUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setReservationDate(reservation.getReservationDate());
        dto.setUserId(reservation.getUser().getId());
        dto.setServiceId(reservation.getService().getId()); // Ustawienie serviceId zamiast getService()
        return dto;
    }

    public Reservation createReservation(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        LocalDateTime reservationDate = reservationDTO.getReservationDate();
        reservation.setReservationDate(reservationDate);
        reservation.setStatus("PENDING");

        Long serviceId = reservationDTO.getServiceId();

        // Find the service based on the provided serviceId
        Services service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + serviceId));

        // Set the service for the reservation
        reservation.setService(service);

        // Find the user based on the provided userId
        User user = userRepository.findById(reservationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + reservationDTO.getUserId()));

        // Set the user for the reservation
        reservation.setUser(user);

        // Calculate the duration in minutes
        Time serviceDuration = service.getDuration();
        int durationMinutes = serviceDuration.toLocalTime().getHour() * 60 + serviceDuration.toLocalTime().getMinute();

        // Check for conflicting reservations
        LocalDateTime endTime = reservationDate.plusMinutes(durationMinutes);
        List<Reservation> existingReservations = reservationRepository.findAll();

        for (Reservation existingReservation : existingReservations) {
            Services existingService = existingReservation.getService();
            if (existingService != null) {
                LocalDateTime existingStart = existingReservation.getReservationDate();
                LocalDateTime existingEnd = existingStart.plusMinutes(
                        existingService.getDuration().toLocalTime().getHour() * 60 +
                                existingService.getDuration().toLocalTime().getMinute()
                );

                if (reservationDate.isBefore(existingEnd) && endTime.isAfter(existingStart)) {
                    throw new ReservationConflictException("This time slot is already booked.");
                }
            }
        }

        // Save the reservation
        return reservationRepository.save(reservation);
    }


    public Reservation updateReservation(Long reservationId, ReservationDTO reservationDTO) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation != null) {
            reservation.setReservationDate(reservationDTO.getReservationDate());

            Long serviceId = reservationDTO.getServiceId();
            Services service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Service not found with id: " + serviceId));

            reservation.setService(service);

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

    public List<LocalTime> getAvailableHours(Long employeeId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Pobranie rezerwacji dla danego pracownika i daty
        List<Reservation> reservations = reservationRepository.findByEmployeeIdAndReservationDateBetween(employeeId, startOfDay, endOfDay);
        List<LocalTime> bookedTimes = new ArrayList<>();

        for (Reservation reservation : reservations) {
            LocalTime startTime = reservation.getReservationDate().toLocalTime();
            Services service = reservation.getService();
            if (service != null) {
                int durationMinutes = service.getDuration().toLocalTime().getHour() * 60 + service.getDuration().toLocalTime().getMinute();
                LocalTime endTime = startTime.plusMinutes(durationMinutes);

                while (startTime.isBefore(endTime)) {
                    bookedTimes.add(startTime);
                    startTime = startTime.plusMinutes(30); // Zakładając, że sloty trwają 30 minut
                }
            }
        }

        List<LocalTime> allTimes = IntStream.range(9, 18) // Godziny pracy od 9:00 do 17:30
                .mapToObj(hour -> LocalTime.of(hour, 0))
                .flatMap(hour -> Stream.of(hour, hour.plusMinutes(30)))
                .collect(Collectors.toList());

        return allTimes.stream()
                .filter(time -> !bookedTimes.contains(time))
                .collect(Collectors.toList());
    }

}

