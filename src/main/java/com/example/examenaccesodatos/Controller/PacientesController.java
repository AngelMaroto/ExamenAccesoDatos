package com.example.examenaccesodatos.Controller;

import com.example.examenaccesodatos.DAO.PacienteDAO;
import com.example.examenaccesodatos.DAO.PacienteDAOimpl;
import com.example.examenaccesodatos.model.Especialidad;
import com.example.examenaccesodatos.model.Paciente;
import com.example.examenaccesodatos.util.HibernateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.hibernate.Session;

import java.security.MessageDigest;
import java.sql.SQLException;

public class PacientesController {

    @FXML private TextField txtDniPac, txtPassPac, txtNombrePac, txtDireccionPac, txtTelefonoPac;
    @FXML private Button btnLimpiarPac, btnCrearPac;

    private final PacienteDAO pacienteDAO = new PacienteDAOimpl();

    @FXML
    private void crearNuevoPaciente(){
        String dni = txtDniPac.getText();
        String pass = txtPassPac.getText();
        String nombre = txtNombrePac.getText();
        String direccion = txtDireccionPac.getText();
        String telefono = txtTelefonoPac.getText();

        if (dni==null||pass==null||nombre==null||direccion==null||telefono==null
                ||dni.isEmpty()||pass.isEmpty()||nombre.isEmpty()||direccion.isEmpty()||telefono.isEmpty()) {
            mostrarError("Algún campo está vacío");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Validación de DNI
            Paciente existente = pacienteDAO.buscarPorDni(dni); // Suponiendo que ya tienes este método
            if (existente != null) {
                mostrarError("El DNI ya está registrado");
                return;
            }

            // Cifrado de contraseña
            String passCifrada = cifrarSHA256(pass);

            Paciente nuevo = new Paciente();
            nuevo.setDni(dni);
            nuevo.setDireccion(direccion);
            nuevo.setNombre(nombre);
            nuevo.setTelefono(telefono);
            nuevo.setPass(passCifrada);

            session.beginTransaction();
            pacienteDAO.crearPaciente(session, nuevo);
            session.getTransaction().commit();

            mostrarInfo("Paciente creado correctamente");
            limpiarCampos();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al crear paciente");
        }
    }

    @FXML
    private void limpiarCampos(){
        txtDniPac.clear();
        txtPassPac.clear();
        txtNombrePac.clear();
        txtDireccionPac.clear();
        txtTelefonoPac.clear();

    }

    // --- Métodos auxiliares para mostrar alertas ---
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para cifrar contraseña SHA-256 en Java
    private String cifrarSHA256(String pass) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pass.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
