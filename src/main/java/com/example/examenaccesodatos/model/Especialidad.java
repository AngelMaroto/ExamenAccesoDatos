package com.example.examenaccesodatos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "especialidades")
public class Especialidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEspecialidad")
    private int idEspecialidad;

    @Column(name = "nombreEspecilidad")
    @JsonProperty("nombreEspecilidad")
    private String nombre;

    public Especialidad() {}

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
