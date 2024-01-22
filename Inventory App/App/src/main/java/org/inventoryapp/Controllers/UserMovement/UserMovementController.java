package org.inventoryapp.Controllers.UserMovement;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import org.inventoryapp.Classes.Movements.Movement;
import org.inventoryapp.Classes.UserMovements.UserMovement;
import org.inventoryapp.SQL.ConnectionSQL;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserMovementController {
    // global variables to make queries
    Statement st;
    ResultSet rs;
    String query;

    // creates a new movement on DB
    public void CreateMovement(UserMovement userMovement){
        try {
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            LocalDateTime date = LocalDateTime.now();
            userMovement.setDate(date);

            query = "insert into movements_user (username,type,date,user) values ('" + userMovement.getUsername() + "','" + userMovement.getType() + "','" + userMovement.getDate() + "','"+userMovement.getUser()+"');";
            st.execute(query);

            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,"El movimiento no se guardo "+err,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // get all movements from DB
    public ArrayList<UserMovement> getAllMovements (LocalDate date, Integer fil) {
        // list of movements to return
        ArrayList<UserMovement> movements = new ArrayList<UserMovement>();
        try{
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // changes the query according to filter
            if (fil == 0) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') ";
            } else if (fil == 1) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') and type like '%login%'";
            } else if (fil == 2) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') and type like '%logout%'";
            } else if (fil == 3) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') and type like '%create%'";
            } else if (fil == 4) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') and type like '%delete%'";
            } else if (fil == 5) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') and type like '%edit%'";
            }

            rs = st.executeQuery(query);

            // add movements to the list
            while(rs.next()){
                UserMovement movement = new UserMovement();
                movement.setUsername(rs.getString("username"));
                movement.setType(rs.getString("type"));
                LocalDateTime tdate = rs.getDate("date").toLocalDate().atTime(rs.getTime("date").toLocalTime());
                movement.setDate(tdate);
                movement.setUser(rs.getString("user"));
                movements.add(movement);
            }

            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,"No se encuentran movimientos "+err,"Error",JOptionPane.ERROR_MESSAGE);
        }
        return movements;
    }

    // get all movements from a specific user
    public ArrayList<UserMovement> getUserMovements (LocalDate date,String username,Integer fil) {
        // list of movements to return
        ArrayList<UserMovement> movements = new ArrayList<UserMovement>();
        try{
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // changes the query according to filter
            if (fil == 0) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') and username = '" + username + "'";
            } else if (fil == 1) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') and username = '" + username + "' and type like '%login%'";
            } else if (fil == 2) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') and username = '" + username + "' and type like '%logout%'";
            } else if (fil == 3) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') and username = '" + username + "' and type like '%create%'";
            } else if (fil == 4) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') and username = '" + username + "' and type like '%delete%'";
            } else if (fil == 5) {
                query = "select * from movements_user where date > DATE('" + date.toString() + "') and username = '" + username + "' and type like '%edit%'";
            }

            rs = st.executeQuery(query);

            // add movements to the list
            while(rs.next()){
                UserMovement movement = new UserMovement();
                movement.setUsername(rs.getString("username"));
                movement.setType(rs.getString("type"));
                LocalDateTime tdate = rs.getDate("date").toLocalDate().atTime(rs.getTime("date").toLocalTime());
                movement.setDate(tdate);
                movement.setUser(rs.getString("user"));
                movements.add(movement);
            }

            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,"No se encuentran movimientos "+err,"Error",JOptionPane.ERROR_MESSAGE);
        }
        return movements;
    }
}
