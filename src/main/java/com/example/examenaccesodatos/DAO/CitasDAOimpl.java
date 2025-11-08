package com.example.examenaccesodatos.DAO;

import com.example.examenaccesodatos.Connection.DBConnection;
import com.example.examenaccesodatos.model.Cita;
import com.example.examenaccesodatos.model.Especialidad;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CitasDAOimpl implements CitasDAO {

    @Override
    public List<Cita> obtenerCitasPorPaciente(int idPaciente) throws SQLException {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.idCita, c.fechaCita, e.idEspecialidad, e.nombreEspecilidad " +
                "FROM Citas c JOIN Especialidades e ON c.idEspecialidad = e.idEspecialidad " +
                "WHERE c.idPaciente = ?";

        try (Connection conn = DBConnection.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPaciente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Cita cita = new Cita();
                cita.setIdCita(rs.getInt("idCita"));
                cita.setFechaCita(rs.getDate("fechaCita").toLocalDate());
                Especialidad especialidad = new Especialidad();
                especialidad.setIdEspecialidad(rs.getInt("idEspecialidad"));
                especialidad.setNombre(rs.getString("nombreEspecilidad"));
                cita.setEspecialidad(especialidad);

                citas.add(cita);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }

    @Override
    public void crearCita(Session session, Cita cita) {
        session.save(cita);
    }

    @Override
    public void borrarCita(Session session, Cita cita) {
        session.beginTransaction();
        session.delete(cita);
        session.getTransaction().commit();
    }

    @Override
    public void modificarCita(Session session, Cita cita) {
        session.update(cita);
    }
}



