package org.inventoryapp.Controllers.Movements;

import org.inventoryapp.Classes.Movements.Movement;
import org.inventoryapp.SQL.ConnectionSQL;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MovementController {
    // global variables to make queries
    Statement st;
    ResultSet rs;
    String query;

    // creates a new movement on DB
    public void CreateMovement(Movement movement) {
        try {
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            LocalDateTime date = LocalDateTime.now();
            movement.setDate(date);

            query = "insert into movements (productid,productname,type,date,username) values ('" + movement.getProductId() + "','" + movement.getProductName() + "','" + movement.getType() + "','" + movement.getDate() + "','"+movement.getUsername()+"');";
            st.execute(query);

            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,"El movimiento no se guardo "+err,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // get all movements from a specific product
    public ArrayList<Movement> getProductMovements (LocalDate date,String id,String name,Integer fil, JTextField username) {
        // list of movements to return
        ArrayList<Movement> movements = new ArrayList<Movement>();
        try{
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // check if a user was given to filter the query
            String uname = "";
            if (!username.getText().isEmpty()){
                uname = "and username = '"+username.getText()+"'";
            }

            // check if it has to be searched by name or by id and changes the query according to filter
            if (name == null) {
                if (fil == 0) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productid = " + id;
                } else if (fil == 1) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productid = " + id + " and type like '%in:%'";
                } else if (fil == 2) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productid = " + id + " and (type like '%out:%' or type like '%sell:%')";
                } else if (fil == 3) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productid = " + id + " and type like '%sell - out of stock%'";
                } else if (fil == 4) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productid = " + id + " and (type like '%name change%' or type like '%up%' or type like '%down%')";
                } else if (fil == 5) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productid = " + id + " and type like '%create%'";
                } else if (fil == 6) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productid = " + id + " and type like '%delete%'";
                }
            } else if (id == null) {
                if (fil == 0) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productname like '%" + name + "%'";
                } else if (fil == 1) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productname like '%" + name + "%' and type like '%in:%'";
                } else if (fil == 2) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productname like '%" + name + "%' and (type like '%out:%' or type like '%sell:%')";
                } else if (fil == 3) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productname like '%" + name + "%' and type like '%sell - out of stock%'";
                } else if (fil == 4) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productname like '%" + name + "%' and (type like '%name change%' or type like '%up%' or type like '%down%')";
                } else if (fil == 5) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productname like '%" + name + "%' and type like '%create%'";
                } else if (fil == 6) {
                    query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and productname like '%" + name + "%' and type like '%delete%'";
                }
            }

            rs = st.executeQuery(query);

            // add movements to the list
            while(rs.next()){
                Movement movement = new Movement();
                movement.setProductId(rs.getLong("productid"));
                movement.setProductName(rs.getString("productname"));
                movement.setType(rs.getString("type"));
                LocalDateTime tdate = rs.getDate("date").toLocalDate().atTime(rs.getTime("date").toLocalTime());
                movement.setDate(tdate);
                movement.setUsername(rs.getString("username"));
                movements.add(movement);
            }

            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,"No se encuentran movimientos "+err,"Error",JOptionPane.ERROR_MESSAGE);
        }
        return movements;
    }

    // get all movements from DB
    public ArrayList<Movement> getAllMovements (LocalDate date,Integer fil, JTextField username) {
        // list of movements to return
        ArrayList<Movement> movements = new ArrayList<Movement>();
        try{
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // check if a user was given to filter the query
            String uname = "";
            if (!username.getText().isEmpty()){
                uname = "and username = '"+username.getText()+"'";
            }

            // changes the query according to filter
            if (fil == 0) {
                query = "select * from movements where date > DATE('" + date.toString() + "') "+uname;
            } else if (fil == 1) {
                query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and type like '%in:%'";
            } else if (fil == 2) {
                query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and (type like '%out:%' or type like '%sell:%')";
            } else if (fil == 3) {
                query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and type like '%sell - out of stock%'";
            } else if (fil == 4) {
                query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and (type like '%name change%' or type like '%up%' or type like '%down%')";
            } else if (fil == 5) {
                query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and type like '%create%'";
            } else if (fil == 6) {
                query = "select * from movements where date > DATE('" + date.toString() + "') "+uname+" and type like '%delete%'";
            }

            rs = st.executeQuery(query);

            // add movements to the list
            while(rs.next()){
                Movement movement = new Movement();
                movement.setProductId(rs.getLong("productid"));
                movement.setProductName(rs.getString("productname"));
                movement.setType(rs.getString("type"));
                LocalDateTime tdate = rs.getDate("date").toLocalDate().atTime(rs.getTime("date").toLocalTime());
                movement.setDate(tdate);
                movement.setUsername(rs.getString("username"));
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
