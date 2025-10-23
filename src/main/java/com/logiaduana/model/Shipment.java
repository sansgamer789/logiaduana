
package com.logiaduana.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "shipments")
public class Shipment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cargo_id")
    private Cargo carga;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "assigned_by")
    private User assignedBy;

    private String status = "CREADA";
    private LocalDateTime lastUpdate = LocalDateTime.now();

    // getters/setters
    public Long getId() { return id; }
    public Cargo getCarga() { return carga; }
    public void setCarga(Cargo carga) { this.carga = carga; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public User getAssignedBy() { return assignedBy; }
    public void setAssignedBy(User assignedBy) { this.assignedBy = assignedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
