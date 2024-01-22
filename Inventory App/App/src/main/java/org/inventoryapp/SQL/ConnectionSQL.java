package org.inventoryapp.SQL;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionSQL {
    public java.sql.Connection connect(){
        // get variables to connect to DB
        DBvars db = new DBvars();

        // setting connection properties
        Properties props = new Properties();
        props.setProperty("user",db.dbUser);
        props.setProperty("password",db.dbPassword);
        props.setProperty("useSSL","true");
        java.sql.Connection connection = null;

        try {
            // connect to the database
            String url = "jdbc:mysql://" + db.dbHost + "/" + db.dbName;
            connection = DriverManager.getConnection(url,props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
