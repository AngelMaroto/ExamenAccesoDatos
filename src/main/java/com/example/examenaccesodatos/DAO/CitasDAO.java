package com.example.examenaccesodatos.DAO;

import com.example.examenaccesodatos.model.Cita;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;

public interface CitasDAO {
    List<Cita> obtenerCitasPorPaciente(int idPaciente) throws SQLException;

    void crearCita(Session session, Cita cita);

    void borrarCita(Session session, Cita cita);

    void modificarCita(Session session, Cita cita);
}

