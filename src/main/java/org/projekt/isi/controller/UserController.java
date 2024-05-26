package org.projekt.isi.controller;

import org.projekt.isi.component.LoginRequest;
import org.projekt.isi.dto.UserDTO;
import org.projekt.isi.entity.User;
import org.projekt.isi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Użytkownik o podanej nazwie już istnieje!");
        }

        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRole("USER");

        userRepository.save(newUser);

        return ResponseEntity.ok("Rejestracja użytkownika przebiegła pomyślnie!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(HttpServletRequest request, @RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nieprawidłowa nazwa użytkownika lub hasło!");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long userId = userRepository.findByUsername(userDetails.getUsername()).getId();
        request.getSession().setAttribute("userId", userId);

        return ResponseEntity.ok("Zalogowano pomyślnie!");
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Wylogowano pomyślnie!");
    }

    @GetMapping("/{username}/id")
    public ResponseEntity<Long> getUserIdByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user.getId());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{username}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            userRepository.delete(user);
            return ResponseEntity.ok("Użytkownik został usunięty pomyślnie!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{username}/emailUpdate")
    public ResponseEntity<?> updateUserEmail(@PathVariable String username, @RequestBody String newEmail) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setEmail(newEmail);
            userRepository.save(user);
            return ResponseEntity.ok("Adres e-mail użytkownika został zaktualizowany pomyślnie!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{username}/passwordUpdate")
    public ResponseEntity<?> updateUserPassword(@PathVariable String username, @RequestBody String newPassword) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return ResponseEntity.ok("Hasło użytkownika zostało zaktualizowane pomyślnie!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
