
package com.logiaduana.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.logiaduana.model.Vehicle;
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {}
