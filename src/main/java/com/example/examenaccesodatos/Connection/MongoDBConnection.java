package com.example.examenaccesodatos.Connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.InputStream;
import java.util.Properties;

public class MongoDBConnection {

    private static MongoClient client;

    public static MongoDatabase getDatabase() {
        Properties props = new Properties();
        try (InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("configuration/Mongo_Database.properties")) {
            props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String user = props.getProperty("user");
        String password = props.getProperty("password");
        String host = props.getProperty("host");
        String port = props.getProperty("port");
        String source = props.getProperty("source");

        String dbName = "ExamenCitasMedicas";

        String uri = String.format("mongodb://%s:%s@%s:%s/?authSource=%s", user, password, host, port, source);

        client = MongoClients.create(uri);
        return client.getDatabase(dbName);
    }
}
