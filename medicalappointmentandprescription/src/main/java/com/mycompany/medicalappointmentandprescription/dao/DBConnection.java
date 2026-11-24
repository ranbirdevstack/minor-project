package com.mycompany.medicalappointmentandprescription.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Properties properties = new Properties();

    static {

        try (InputStream input = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "db.properties file not found!"
                );
            }

            properties.load(input);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error loading db.properties", e
            );
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");

        String[] passwords = {
            properties.getProperty("db.password_partner_1"),
            properties.getProperty("db.password_partner_2")
        };

        SQLException lastException = null;

        for (String password : passwords) {

            if (password == null) {
                continue;
            }

            try {

                return DriverManager.getConnection(
                        url,
                        username,
                        password
                );

            } catch (SQLException e) {

                lastException = e;
            }
        }

        throw new SQLException(
                "Unable to connect to MySQL. Check username and passwords.",
                lastException
        );
    }
}