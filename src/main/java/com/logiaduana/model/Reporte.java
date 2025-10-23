package com.logiaduana.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigoTracking;

    private String descripcion;

    private LocalDateTime fechaGeneracion;

    @ManyToOne
    @JoinColumn(name = "carga_id")
    private Carga carga; // Relación con la carga asociada

    public Reporte() {}

    public Reporte(String codigoTracking, String descripcion, LocalDateTime fechaGeneracion, Carga carga) {
        this.codigoTracking = codigoTracking;
        this.descripcion = descripcion;
        this.fechaGeneracion = fechaGeneracion;
        this.carga = carga;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoTracking() {
        return codigoTracking;
    }

    public void setCodigoTracking(String codigoTracking) {
        this.codigoTracking = codigoTracking;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Carga getCarga() {
        return carga;
    }

    public void setCarga(Carga carga) {
        this.carga = carga;
    }
}
