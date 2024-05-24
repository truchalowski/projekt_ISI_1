package org.projekt.isi.repository;

import org.projekt.isi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Metoda do sprawdzania istnienia użytkownika na podstawie nazwy użytkownika
    boolean existsByUsername(String username);
    User findByUsername(String username);

}