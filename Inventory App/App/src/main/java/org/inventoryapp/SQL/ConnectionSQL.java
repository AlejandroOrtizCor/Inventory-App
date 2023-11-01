package org.inventoryapp.SQL;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionSQL {
    public java.sql.Connection connect(){
        // database configuration variables
        String dbHost = "aws.connect.psdb.cloud";
        String dbUser = "q5rjxx6w1m10s7ik4s3f";
        String dbPassword = "pscale_pw_ukhnuAsesQDZEdHnn9XF1nxci2HWGYDwvnMrRbvVzwP";
        String dbName = "inventory";

        // setting connection properties
        Properties props = new Properties();
        props.setProperty("user",dbUser);
        props.setProperty("password",dbPassword);
        props.setProperty("useSSL","true");
        java.sql.Connection connection = null;

        try {
            // connect to the database
            String url = "jdbc:mysql://" + dbHost + "/" + dbName;
            connection = DriverManager.getConnection(url,props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
