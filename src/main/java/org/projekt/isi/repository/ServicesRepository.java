package org.projekt.isi.repository;

import org.projekt.isi.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicesRepository extends JpaRepository<Services, Long> {
    // Możesz dodać niestandardowe metody repozytorium, jeśli są potrzebne
}
