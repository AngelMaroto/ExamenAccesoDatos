package com.example.examenaccesodatos.Controller;

import com.example.examenaccesodatos.DAO.*;
import com.example.examenaccesodatos.model.Cita;
import com.example.examenaccesodatos.model.Especialidad;
import com.example.examenaccesodatos.model.Paciente;
import com.example.examenaccesodatos.util.HibernateUtil;
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
import org.hibernate.Session;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CitasController {

    @FXML private TextField txtDni, txtNombre, txtDireccion, txtTelefono, txtNumero;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<Especialidad> cmbEspecialidad;
    @FXML private TableView<Cita> tableCitas;
    @FXML private TableColumn<Cita, Integer> colNumero;
    @FXML private TableColumn<Cita, LocalDate> colFecha;
    @FXML private TableColumn<Cita, String> colEspecialidad;
    @FXML private Button btnLimpiar, btnBorrar, btnNueva, btnModificar;

    PacienteDAO pacienteDAO = new PacienteDAOimpl();
    CitasDAO citasDAO = new CitasDAOimpl();
    private Paciente pac;
    CitasMongoDAO citasMongoDAO = new CitasMongoDAOimpl();

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @FXML
    public void initialize() {
        colNumero.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdCita()).asObject());
        colFecha.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFechaCita()));
        colEspecialidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEspecialidad().toString()));

        cargaEspecialidad();

        txtDni.setOnAction(e -> {
            try {
                buscarPacientePorDni();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        tableCitas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosCita(newSelection);
            }
        });
    }

    private void buscarPacientePorDni() throws SQLException {
        String dni = txtDni.getText();
        Paciente paciente = pacienteDAO.buscarPorDni(dni);
        if (paciente != null) {
            pac = paciente;
            txtNombre.setText(paciente.getNombre());
            txtDireccion.setText(paciente.getDireccion());
            txtTelefono.setText(paciente.getTelefono());
            txtNombre.setDisable(true);
            txtDireccion.setDisable(true);
            txtTelefono.setDisable(true);
        } else {
            mostrarError("Paciente no encontrado con DNI: " + dni);
        }
    }

    @FXML
    private void verCitasPaciente() throws SQLException {
        int idPaciente = pac.getIdPaciente();
        List<Cita> citas = citasDAO.obtenerCitasPorPaciente(idPaciente);

        ObservableList<Cita> citasFX = FXCollections.observableArrayList(citas);
        tableCitas.setItems(citasFX);

        boolean tieneCitaHoy = citas.stream().anyMatch(c -> c.getFechaCita().equals(LocalDate.now()));
        if (tieneCitaHoy) {
            mostrarInfo("¡El paciente tiene una cita para hoy!");
        }
        if (citas.isEmpty()) {
            mostrarWarning("El paciente no tiene citas.");
        }
    }

    private void cargarDatosCita(Cita cita) {
        txtNumero.setText(String.valueOf(cita.getIdCita()));
        dpFecha.setValue(cita.getFechaCita());
        cmbEspecialidad.getSelectionModel().select(cita.getEspecialidad());
    }

    @FXML
    private void limpiarCamposCita() {
        txtNumero.clear();
        dpFecha.setValue(null);
        cmbEspecialidad.getSelectionModel().select(null);
        tableCitas.getSelectionModel().clearSelection();
    }

    @FXML
    private void crearNuevCita() {
        if (pac == null) {
            mostrarError("Primero busca y selecciona el paciente.");
            return;
        }
        LocalDate fecha = dpFecha.getValue();
        Especialidad especialidadCombo = cmbEspecialidad.getValue();
        if (fecha == null || especialidadCombo == null) {
            mostrarError("Completa los campos de fecha y especialidad.");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Especialidad especialidadHibernate = session.get(Especialidad.class, especialidadCombo.getIdEspecialidad());
            if (especialidadHibernate == null) {
                mostrarError("La especialidad seleccionada no existe en la base de datos.");
                return;
            }
            session.beginTransaction();
            Cita nuevaCita = new Cita();
            nuevaCita.setFechaCita(fecha);
            nuevaCita.setEspecialidad(especialidadHibernate);
            nuevaCita.setPaciente(pac);
            citasDAO.crearCita(session, nuevaCita);
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error guardando la cita con Hibernate.");
            return;
        }

        // citasMongoDAO.crearCitaMongo(nuevaCita);  // Si tienes método correcto

        actualizarCitasTableView();
        limpiarCamposCita();
        mostrarInfo("¡Nueva cita guardada!");
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarWarning(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
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

    @FXML
    private void borrarCitaSeleccionada() {
        Cita cita = tableCitas.getSelectionModel().getSelectedItem();
        if (cita == null) {
            mostrarError("Selecciona una cita para borrar.");
            return;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            citasDAO.borrarCita(session, cita);
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al borrar la cita.");
            return;
        }
        actualizarCitasTableView();
    }

    @FXML
    private void modificarCitaSeleccionada() {
        Cita citaSeleccionada = tableCitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada == null) {
            mostrarError("Selecciona una cita para modificar.");
            return;
        }
        LocalDate fechaModificada = dpFecha.getValue();
        Especialidad espModificada = cmbEspecialidad.getValue();
        if (fechaModificada == null || espModificada == null) {
            mostrarError("Elige bien fecha y especialidad.");
            return;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Especialidad espHibernate = session.get(Especialidad.class, espModificada.getIdEspecialidad());
            if (espHibernate == null) {
                mostrarError("La especialidad seleccionada no existe en la base de datos.");
                return;
            }
            session.beginTransaction();
            citaSeleccionada.setFechaCita(fechaModificada);
            citaSeleccionada.setEspecialidad(espHibernate);
            citaSeleccionada.setPaciente(pac);
            citasDAO.modificarCita(session, citaSeleccionada);
            session.getTransaction().commit();
            mostrarInfo("Cita actualizada");
            actualizarCitasTableView();
            limpiarCamposCita();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al modificar la cita.");
        }
    }

    private void actualizarCitasTableView() {
        try {
            List<Cita> citas = citasDAO.obtenerCitasPorPaciente(pac.getIdPaciente());
            tableCitas.setItems(FXCollections.observableArrayList(citas));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargaEspecialidad() {
        cmbEspecialidad.getItems().clear();
        ArrayList<Especialidad> especialidad;
        try  {
            especialidad = JSON_MAPPER.readValue(new File("src/main/resources/JSON/especialidades.json"),
                    JSON_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Especialidad.class));
            ObservableList<Especialidad> datos = FXCollections.observableArrayList(especialidad);
            cmbEspecialidad.setItems(datos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
