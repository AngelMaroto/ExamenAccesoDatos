package com.example.examenaccesodatos.DAO;

import com.example.examenaccesodatos.model.Paciente;
import org.hibernate.Session;

import java.sql.SQLException;

public interface PacienteDAO {

    Paciente buscarPorDni(String dni) throws SQLException;
    Paciente crearPaciente(Session session, Paciente pac);
}
