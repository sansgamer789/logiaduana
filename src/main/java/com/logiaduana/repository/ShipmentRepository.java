
package com.logiaduana.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.logiaduana.model.Shipment;
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {}
