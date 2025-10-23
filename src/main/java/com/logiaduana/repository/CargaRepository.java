package com.logiaduana.repository;

import com.logiaduana.model.Carga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CargaRepository extends JpaRepository<Carga, Long> {
    Optional<Carga> findByNumeroGuia(String numeroGuia);
}
