package com.example.examenaccesodatos.DAO;

import com.example.examenaccesodatos.Connection.MongoDBConnection;
import com.example.examenaccesodatos.model.Cita;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class CitasMongoDAOimpl implements CitasMongoDAO{
    @Override
    public void crearCitaMongo(Cita cita) {
        try {
            MongoCollection<Document> coleccion = MongoDBConnection.getDatabase().getCollection("CitasMedicas");

            Document doc = new Document()
                    .append("idCita", cita.getIdCita())
                    .append("fechaCita", cita.getFechaCita().toString())
                    .append("especialidad", cita.getEspecialidad().getNombre())
                    .append("idPaciente", cita.getPaciente().getIdPaciente());

            coleccion.insertOne(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
