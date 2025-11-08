package com.example.examenaccesodatos.Controller;

import com.example.examenaccesodatos.Connection.DBConnection;
import com.example.examenaccesodatos.model.Cita;
import com.example.examenaccesodatos.model.Especialidad;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CitasController {
    /*
    IDS Interfaz
    txtDni
    txtNombre
    txtDireccion
    txtTelefono
    btnVer
    txtNumero
    dpFecha
    cmbEspecialidad
    tableCitas
    colNumero
    colFecha
    colEspecialidad
    btnLimpiar
    btnNueva
    btnBorrar
    btnModificar*/

    @FXML private TextField txtDni, txtNombre, txtDireccion, txtTelefono;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<Especialidad> cmbEspecialidad;
    @FXML private TableView<Cita> tableCitas;
    @FXML private TableColumn<Cita, Integer> colNumero;
    @FXML private TableColumn<Cita, LocalDate> colFecha;
    @FXML private TableColumn<Cita, String> colEspecialidad;

    @FXML public void initialize(){
        colNumero.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdCita()).asObject());
        colFecha.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFechaCita()));
        colEspecialidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEspecialidad().toString()));

        cargaEspecialidad();

        txtDni.setOnAction(e -> buscarPacientePorDni());

    }

    private void buscarPacientePorDni() {
        String dni = txtDni.getText();
        if (dni == null || dni.isEmpty()) return;

        try (Connection conn = DBConnection.conectar();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM Pacientes WHERE dni = ?")) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtNombre.setText(rs.getString("Nombre"));
                txtDireccion.setText(rs.getString("Direccion"));
                txtTelefono.setText(rs.getString("Telefono"));

                // Deshabilita los campos para que no puedan modificarse
                txtNombre.setDisable(true);
                txtDireccion.setDisable(true);
                txtTelefono.setDisable(true);
            } else {
                mostrarError("El paciente no existe.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarError("Error al conectar con la base de datos.");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }



    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private void cargaEspecialidad() {
        cmbEspecialidad.getItems().clear();
        ArrayList<Especialidad> especialidad;
        try  {
            especialidad = JSON_MAPPER.readValue(new File("src/main/resources/JSON/especialidades.json"),
                    JSON_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Especialidad.class));
            ObservableList<Especialidad> datos = FXCollections.observableArrayList(especialidad);
            cmbEspecialidad.setItems(datos);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
