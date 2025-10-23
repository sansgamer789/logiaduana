
package com.logiaduana.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "tracking_events")
public class TrackingEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    private LocalDateTime timestamp = LocalDateTime.now();
    private String status;
    private String note;

    // getters/setters
    public Long getId() { return id; }
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
