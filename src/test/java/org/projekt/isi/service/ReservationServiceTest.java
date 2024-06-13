package org.projekt.isi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.projekt.isi.dto.ReservationDTO;
import org.projekt.isi.entity.*;
import org.projekt.isi.exceptions.ReservationConflictException;
import org.projekt.isi.repository.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServicesRepository serviceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCancelReservation_Success() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act
        reservationService.cancelReservation(1L);

        // Assert
        assertEquals("CANCELLED", reservation.getStatus());
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    public void testConfirmReservation_Success() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus("PENDING");

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Zwraca zapisany obiekt

        // Act
        Reservation confirmedReservation = reservationService.confirmReservation(1L);

        // Assert
        assertNotNull(confirmedReservation); // Upewnij się, że confirmedReservation nie jest nullem
        assertEquals("CONFIRMED", confirmedReservation.getStatus()); // Sprawdź, czy status jest CONFIRMED
        verify(reservationRepository, times(1)).save(reservation);
    }


    @Test
    public void testGetAvailableHours() {
        // Arrange
        Long employeeId = 1L;
        LocalDate date = LocalDate.now();

        Reservation reservation = new Reservation();
        reservation.setReservationDate(date.atTime(10, 0));

        Services service = new Services();
        service.setDuration(Time.valueOf(LocalTime.of(1, 0))); // 1 hour duration

        reservation.setService(service);

        when(reservationRepository.findByEmployeeIdAndReservationDateBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(reservation));

        // Act
        List<LocalTime> availableHours = reservationService.getAvailableHours(employeeId, date);

        // Assert
        assertFalse(availableHours.contains(LocalTime.of(10, 0)));
        assertFalse(availableHours.contains(LocalTime.of(10, 30)));
        assertTrue(availableHours.contains(LocalTime.of(9, 0)));
        assertTrue(availableHours.contains(LocalTime.of(11, 0)));
    }

    @Test
    public void testGetAvailableHours_Conflict() {
        // Arrange
        Long employeeId = 1L;
        LocalDate date = LocalDate.now();

        // Utwórz istniejące rezerwacje o różnych godzinach
        Reservation existingReservation1 = new Reservation();
        existingReservation1.setReservationDate(date.atTime(9, 0)); // Konfliktująca godzina

        Reservation existingReservation2 = new Reservation();
        existingReservation2.setReservationDate(date.atTime(11, 0)); // Godzina bez konfliktu

        // Utwórz usługę
        Services service = new Services();
        service.setDuration(Time.valueOf(LocalTime.of(1, 0))); // 1 godzina trwania usługi

        existingReservation1.setService(service);
        existingReservation2.setService(service);

        // Kiedy metoda findByEmployeeIdAndReservationDateBetween jest wywoływana z dowolnymi argumentami, zwróć istniejące rezerwacje
        when(reservationRepository.findByEmployeeIdAndReservationDateBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(existingReservation1, existingReservation2));

        // Act
        List<LocalTime> availableHours = reservationService.getAvailableHours(employeeId, date);

        // Assert
        assertFalse(availableHours.contains(LocalTime.of(9, 0))); // Sprawdź, czy godzina 9:00 jest filtrowana
        assertTrue(availableHours.contains(LocalTime.of(10, 0))); // Upewnij się, że godzina 10:00 nie jest filtrowana
        assertFalse(availableHours.contains(LocalTime.of(11, 0))); // Sprawdź, czy godzina 11:00 jest filtrowana
    }
}
