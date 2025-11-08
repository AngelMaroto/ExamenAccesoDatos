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

/**
 * Implementación del DAO (Data Access Object) para la entidad Cita.
 * Esta clase combina el uso de JDBC (para lecturas de datos) y Hibernate (para escrituras y modificaciones).
 *
 * - JDBC: usado para leer citas desde la base de datos MySQL.
 * - Hibernate: usado para crear, modificar y eliminar citas.
 */
public class CitasDAOimpl implements CitasDAO {

    /**
     * Obtiene todas las citas de un paciente concreto desde MySQL.
     * Este método usa JDBC y devuelve una lista de objetos Cita.
     */
    @Override
    public List<Cita> obtenerCitasPorPaciente(int idPaciente) throws SQLException {
        List<Cita> citas = new ArrayList<>();

        // Consulta SQL que une las tablas Citas y Especialidades
        String sql = "SELECT c.idCita, c.fechaCita, e.idEspecialidad, e.nombreEspecilidad " +
                "FROM Citas c JOIN Especialidades e ON c.idEspecialidad = e.idEspecialidad " +
                "WHERE c.idPaciente = ?";

        // try-with-resources: cierra la conexión automáticamente
        try (Connection conn = DBConnection.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPaciente); // Reemplazamos el parámetro "?" con el idPaciente
            ResultSet rs = ps.executeQuery();

            // Recorremos los resultados para construir la lista de Cita
            while (rs.next()) {
                Cita cita = new Cita();
                cita.setIdCita(rs.getInt("idCita"));
                cita.setFechaCita(rs.getDate("fechaCita").toLocalDate());

                // Creamos una Especialidad para asociarla a la cita
                Especialidad especialidad = new Especialidad();
                especialidad.setIdEspecialidad(rs.getInt("idEspecialidad"));
                especialidad.setNombre(rs.getString("nombreEspecilidad"));
                cita.setEspecialidad(especialidad);

                citas.add(cita); // Añadimos la cita a la lista
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }

    /**
     * Crea una nueva cita en la base de datos usando Hibernate.
     * Recibe una sesión abierta y un objeto Cita, y lo persiste.
     */
    @Override
    public void crearCita(Session session, Cita cita) {
        // Hibernate maneja automáticamente el INSERT
        session.save(cita);
    }

    /**
     * Elimina una cita de la base de datos con Hibernate.
     * Abre una transacción, borra la cita y la confirma.
     */
    @Override
    public void borrarCita(Session session, Cita cita) {
        session.beginTransaction();   // Inicia la transacción
        session.delete(cita);         // Elimina el registro
        session.getTransaction().commit(); // Confirma el borrado
    }

    /**
     * Modifica una cita existente con Hibernate.
     * IMPORTANTE: el objeto Cita debe tener todos sus datos completos,
     * especialmente el paciente, o la FK quedará en NULL.
     */
    @Override
    public void modificarCita(Session session, Cita cita) {
        // update() sincroniza los cambios del objeto con la BD
        session.update(cita);

        // También se podría usar session.merge(cita);
        // merge() es más seguro cuando el objeto viene "desconectado" del contexto de Hibernate.
    }
}
