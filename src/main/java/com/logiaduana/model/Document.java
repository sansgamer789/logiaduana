
package com.logiaduana.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "documents")
public class Document {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;

    private String docType;
    private String path;
    private LocalDateTime uploadedAt = LocalDateTime.now();

    // getters/setters
    public Long getId() { return id; }
    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }
    public String getDocType() { return docType; }
    public void setDocType(String docType) { this.docType = docType; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}
