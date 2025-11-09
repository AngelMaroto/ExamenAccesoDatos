package com.example.examenaccesodatos.DAO;

import com.example.examenaccesodatos.Connection.DBConnection;
import com.example.examenaccesodatos.model.Paciente;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementación del DAO de pacientes.
 * Se encarga de acceder a la tabla "Pacientes" de MySQL usando JDBC.
 *
 * En este examen, solo se necesita buscar un paciente por su DNI
 * (para rellenar los datos del formulario al iniciar sesión).
 */
public class PacienteDAOimpl implements PacienteDAO {

    /**
     * Busca un paciente por su DNI en la base de datos MySQL.
     *
     * @param dni DNI del paciente a buscar.
     * @return un objeto Paciente si existe, o null si no se encuentra.
     */
    @Override
    public Paciente buscarPorDni(String dni) throws SQLException {
        Paciente paciente = null;

        // Consulta SQL con parámetro: evita inyecciones SQL
        String sql = "SELECT * FROM Pacientes WHERE dni = ?";

        // try-with-resources: cierra la conexión automáticamente al salir del bloque
        try (Connection conn = DBConnection.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Sustituimos el parámetro ? por el DNI real
            ps.setString(1, dni);

            // Ejecutamos la consulta
            ResultSet rs = ps.executeQuery();

            // Si existe un resultado, creamos un objeto Paciente con los datos
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

        // Si no se encontró, se devolverá null
        return paciente;
    }

    @Override
    public Paciente crearPaciente(Session session, Paciente pac) {
        session.save(pac);
        return pac;
    }
}
