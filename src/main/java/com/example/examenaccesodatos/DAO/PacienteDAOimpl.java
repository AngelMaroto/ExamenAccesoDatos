package com.example.examenaccesodatos.DAO;

import com.example.examenaccesodatos.Connection.DBConnection;
import com.example.examenaccesodatos.model.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PacienteDAOimpl implements PacienteDAO{

    @Override
    public Paciente buscarPorDni(String dni) throws SQLException {
            Paciente paciente = null;
            String sql = "SELECT * FROM Pacientes WHERE dni = ?";
            try (Connection conn = DBConnection.conectar();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, dni);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    paciente = new Paciente();
                    paciente.setIdPaciente(rs.getInt("idPaciente"));
                    paciente.setDni(rs.getString("dni"));
                    paciente.setNombre(rs.getString("Nombre"));
                    paciente.setDireccion(rs.getString("Direccion"));
                    paciente.setTelefono(rs.getString("Telefono"));
                    paciente.setPass(rs.getString("Pass"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return paciente;
        }
    }

