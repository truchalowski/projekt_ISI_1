package org.projekt.isi.service;

import org.projekt.isi.dto.EmployeeDTO;
import org.projekt.isi.entity.Employee;
import org.projekt.isi.entity.User;
import org.projekt.isi.exceptions.EmployeeNotFoundException;
import org.projekt.isi.repository.EmployeeRepository;
import org.projekt.isi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO getEmployeeById(Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            return convertToDTO(optionalEmployee.get());
        } else {
            throw new EmployeeNotFoundException("Employee not found with ID: " + employeeId);
        }
    }

    public Employee createEmployee(EmployeeDTO employeeDTO) {
        // Create user with default username and password
        User user = new User();
        user.setUsername(employeeDTO.getFirstName().substring(0, 1).toLowerCase() + employeeDTO.getLastName().toLowerCase());
        user.setPassword(passwordEncoder.encode("aaa12456"));
        user.setEmail(employeeDTO.getEmail()); // Set email
        user.setRole("EMPLOYEE");
        user = userRepository.save(user);

        // Create employee and associate with the user
        Employee employee = new Employee();
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setUser(user); // Associate user with employee
        return employeeRepository.save(employee);
    }



    // Metoda pomocnicza do generowania nazwy użytkownika
    private String generateUsername(String firstName, String lastName) {
        String username = firstName.substring(0, 1).toLowerCase() + lastName.toLowerCase();
        // Jeśli nazwa użytkownika już istnieje, dodajemy cyfrę na końcu
        int suffix = 1;
        while (userRepository.findByUsername(username) != null) {
            username = firstName.substring(0, 1).toLowerCase() + lastName.toLowerCase() + suffix;
            suffix++;
        }
        return username;
    }

    public EmployeeDTO updateEmployee(Long employeeId, EmployeeDTO employeeDTO) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setFirstName(employeeDTO.getFirstName());
            employee.setLastName(employeeDTO.getLastName());
            Employee updatedEmployee = employeeRepository.save(employee);
            return convertToDTO(updatedEmployee);
        } else {
            throw new EmployeeNotFoundException("Employee not found with ID: " + employeeId);
        }
    }

    public void deleteEmployee(Long employeeId) {
        if (employeeRepository.existsById(employeeId)) {
            employeeRepository.deleteById(employeeId);
        } else {
            throw new EmployeeNotFoundException("Employee not found with ID: " + employeeId);
        }
    }

    public EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId()); // Dodaj ID pracownika do DTO
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getUser().getEmail()); // Pobranie emaila związanego użytkownika
        return dto;
    }

}
