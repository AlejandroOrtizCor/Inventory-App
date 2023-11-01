package org.inventoryapp.Controllers.User;

import org.inventoryapp.Classes.User.User;
import org.inventoryapp.SQL.ConnectionSQL;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserController {
    // global variables to make queries
    Statement st;
    ResultSet rs;
    String query;

    // creates a new user on DB
    public boolean CreateUser(User user){
        // variable to check if a user is invalid to be added
        boolean invalid = false;
        try {
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            LocalDateTime date = LocalDateTime.now();
            user.setInDate(date);

            // checks if the username exists on the users table
            query = "select * from user where username = '"+user.getUsername()+"'";
            rs = st.executeQuery(query);

            if (rs.next()){
                invalid=true;
            }

            // if a user has the given username it will return that is invalid
            if (invalid == true){
                JOptionPane.showMessageDialog(null,"El nombre ya está en uso","Error",JOptionPane.ERROR_MESSAGE);
                return invalid;
            }else {
                // creates product
                query = "insert into user (name,username,password,role,indate) values ('" + user.getName().trim() + "','" + user.getUsername().trim() + "','" + user.getPassword() + "','" + user.getRole() + "','" + user.getInDate() + "');";
                st.execute(query);

                JOptionPane.showMessageDialog(null, "El usuario se guardó correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }

            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,err,"Error",JOptionPane.ERROR_MESSAGE);
        }
        return invalid;
    }

    // get user by username
    public User SearchByUsername (String username){
        // creates the user to return
        User user = new User();

        try {
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // makes the query
            query = "select * from user where username = '"+username+"'";
            rs = st.executeQuery(query);

            // creates the attributes of the user to return
            if (rs.next()){
                user.setName(rs.getString("name"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setId(rs.getInt("id"));
            }

            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,err,"Error",JOptionPane.ERROR_MESSAGE);
        }

        return user;
    }

    // get all users from DB
    public ArrayList<User> SearchAllUsers (){
        // list of users to return
        ArrayList<User> users = new ArrayList<User>();

        try {
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // make the query
            query = "select * from user";
            rs = st.executeQuery(query);

            // fill the list with each user found on query
            while (rs.next()){
                User user = new User();
                user.setName(rs.getString("name"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }

            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,err,"Error",JOptionPane.ERROR_MESSAGE);
        }

        return users;
    }

    // edits an existing user on DB
    public ArrayList<String> EditUser(String id, User user) {
        // list to register the changes on user to save the movement
        ArrayList<String> res = new ArrayList<String>();
        try {
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            query = "select * from user where username = '"+user.getUsername()+"'";
            rs = st.executeQuery(query);

            // check if the new username is already on use
            if (rs.next() && rs.getInt("id")!=Integer.parseInt(id)){
                JOptionPane.showMessageDialog(null, "El nombre ya está en uso", "Error", JOptionPane.ERROR_MESSAGE);
                res.add("1");
            }else{
                res.add("0");
            }

            // if username isn't used
            if (res.get(0)=="0") {

                query = "select * from user where id = '" + id + "'";
                rs = st.executeQuery(query);

                // variables to save the changed values
                Integer nameDiff = 0;
                Integer passDiff = 0;
                Integer roleDiff = 0;

                // compares the values of the new product with the older one to save differences
                if (rs.next()) {
                    if (!rs.getString("name").equals(user.getName())) {
                        nameDiff++;
                    }
                    if (!rs.getString("password").equals(user.getPassword())) {
                        passDiff++;
                    }
                    if (!rs.getString("role").equals(user.getRole())) {
                        roleDiff++;
                    }
                }

                // check if there are changes on the product
                if (nameDiff == 0 && passDiff == 0 && roleDiff==0) {
                    JOptionPane.showMessageDialog(null, "No has cambiado nada", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // add to list the changes of the user
                    res.add(nameDiff.toString());
                    res.add(rs.getString("name"));
                    res.add(passDiff.toString());
                    res.add(rs.getString("password"));
                    res.add(roleDiff.toString());
                    res.add(rs.getString("role"));
                    res.add(rs.getString("username"));

                    // make the query to edit the product
                    query = "update user set name = '" + user.getName().trim() + "',username = '" + user.getUsername() + "',password = '" + user.getPassword() + "',role = '" + user.getRole() + "' where id = '" + id + "';";
                    st.execute(query);
                    JOptionPane.showMessageDialog(null, "El usuario se editó correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,err,"Error",JOptionPane.ERROR_MESSAGE);
        }
        return res;
    }

    // deletes a user by its id
    public void DeleteUser(String id) {
        try {
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // make the query to delete it
            query = "delete from user where id = '" + id +"';";
            st.execute(query);

            st.close();
            connection.close();
            JOptionPane.showMessageDialog(null, "El usario se borró correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,"El usario no se borró "+err,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
