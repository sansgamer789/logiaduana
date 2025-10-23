package com.logiaduana.repository;

import com.logiaduana.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    Optional<Reporte> findByCodigoTracking(String codigoTracking);
}
