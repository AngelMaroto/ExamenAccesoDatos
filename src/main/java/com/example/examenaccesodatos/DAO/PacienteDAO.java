package com.example.examenaccesodatos.DAO;

import com.example.examenaccesodatos.model.Paciente;

import java.sql.SQLException;

public interface PacienteDAO {

    Paciente buscarPorDni(String dni) throws SQLException;
}
