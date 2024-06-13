package org.projekt.isi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.projekt.isi.dto.EmployeeDTO;
import org.projekt.isi.entity.Employee;
import org.projekt.isi.exceptions.EmployeeNotFoundException;
import org.projekt.isi.repository.EmployeeRepository;
import org.projekt.isi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllEmployees() {
        // Arrange
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee());
        employees.add(new Employee());
        when(employeeRepository.findAll()).thenReturn(employees);

        // Act
        List<EmployeeDTO> employeeDTOs = employeeService.getAllEmployees();

        // Assert
        assertEquals(2, employeeDTOs.size());
    }

    @Test
    public void testGetEmployeeById_Success() {
        // Arrange
        Employee employee = new Employee();
        employee.setId(1L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        // Act
        EmployeeDTO employeeDTO = employeeService.getEmployeeById(1L);

        // Assert
        assertNotNull(employeeDTO);
        assertEquals(1L, employeeDTO.getId());
    }

    @Test
    public void testGetEmployeeById_EmployeeNotFound() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(1L));
    }
}
