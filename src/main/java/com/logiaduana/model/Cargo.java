package com.logiaduana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cargas")
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;
    private Double peso;
    private Double volumen;
    private String embalaje;

    public Cargo() {}

    public Cargo(String descripcion, Double peso, Double volumen, String embalaje) {
        this.descripcion = descripcion;
        this.peso = peso;
        this.volumen = volumen;
        this.embalaje = embalaje;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }

    public Double getVolumen() { return volumen; }
    public void setVolumen(Double volumen) { this.volumen = volumen; }

    public String getEmbalaje() { return embalaje; }
    public void setEmbalaje(String embalaje) { this.embalaje = embalaje; }
}
