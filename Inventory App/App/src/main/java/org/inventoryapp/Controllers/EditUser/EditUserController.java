package org.inventoryapp.Controllers.EditUser;

import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Classes.UserMovements.UserMovement;
import org.inventoryapp.Controllers.User.UserController;
import org.inventoryapp.Controllers.UserMovement.UserMovementController;

import javax.swing.*;
import java.util.ArrayList;

public class EditUserController {
    // id of the user to edit
    String id = "";

    // restart the gui
    public void restart(JTextField username, JTextField name, JTextField password, JComboBox role, JButton search, JButton edit, JButton delete){
        username.setText(null);
        name.setText(null);
        password.setText(null);
        role.setSelectedIndex(0);

        search.setText("Buscar");
        edit.setEnabled(false);
        delete.setEnabled(false);
        name.setEnabled(false);
        username.setEditable(true);
        password.setEnabled(false);
        role.setEnabled(false);
    }

    // search the user to be edited
    public void searchUser(JTextField username, JTextField name, JTextField password, JComboBox role, JButton search, JButton edit, JButton delete, User activeUser){
        String usern = username.getText();
        // check if the username field is empty
        if (usern.isEmpty()){
            JOptionPane.showMessageDialog(null, "Ingresa el nombre de usuario", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User user;
        UserController userController = new UserController();
        user = userController.SearchByUsername(usern);
        // check if user exists
        if (user.getName()==null){
            JOptionPane.showMessageDialog(null, "No se encuentra el usuario", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // make sure you're not editing the user that you're using
        if (user.getUsername().equals(activeUser.getUsername())){
            JOptionPane.showMessageDialog(null, "No puedes editar el usuario con el que iniciaste sesion, cierra sesion y hazlo desde otro perfil", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // make sure you're not editing the admin user
        if (user.getUsername().equals("admin")){
            JOptionPane.showMessageDialog(null, "No puedes editar el usuario administrador", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // if the user is found fills and enables the text fields
        id = user.getId().toString();
        name.setText(user.getName());
        password.setText(user.getPassword());
        role.setSelectedItem(user.getRole());

        search.setText("Nueva Busqueda");
        edit.setEnabled(true);
        delete.setEnabled(true);
        name.setEnabled(true);
        username.setEditable(false);
        password.setEnabled(true);
        role.setEnabled(true);
    }

    // check if the user is ok to be edited on the DB
    public void editUser(JTextField username, JTextField name, JTextField password, JComboBox role, JButton search, JButton edit, JButton delete, User userMov){
        User user = new User();
        // check if there is empty spaces
        if (name.getText().isEmpty() || password.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Tienes campos en blanco", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // check if username is invalid
        if (username.getText().equals("Debes poner al menos un nombre y un apellido")){
            JOptionPane.showMessageDialog(null, "Debes poner un nombre valido para que se genere el usuario", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // check if name is numeric
        try {
            Integer.parseInt(name.getText());

            JOptionPane.showMessageDialog(null, "No se puede poner un nombre de solo numeros", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception err) {
            // check if password length is less than 8 characters
            if (password.getText().trim().length()<=7){
                JOptionPane.showMessageDialog(null, "La contraseña debe tener como minimo 8 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // create user
            user.setName(name.getText());
            user.setUsername(username.getText());
            user.setPassword(password.getText());
            if (role.getSelectedIndex() == 0){
                user.setRole("Employee");
            }else{
                user.setRole("Admin");
            }
            ArrayList<String> res;
            UserController userController = new UserController();
            res = userController.EditUser(id.toString(),user);
            // register the movement if user is correctly edited
            if (Integer.parseInt(res.get(0)) == 0 && res.size()>1) {
                UserMovement userMovement = new UserMovement();
                userMovement.setUsername(user.getUsername());
                userMovement.setUser(userMov.getUsername());
                UserMovementController userMovementController = new UserMovementController();
                // create the type of movement according to the done action
                if (Integer.parseInt(res.get(1))==1){
                    userMovement.setType("edit name:"+res.get(2).toString()+"/"+res.get(7).toString()+"-"+user.getName()+"/"+user.getUsername());
                    userMovementController.CreateMovement(userMovement);
                }
                if (Integer.parseInt(res.get(3))==1){
                    userMovement.setType("edit password:"+res.get(4).toString()+"¿"+user.getPassword());
                    userMovementController.CreateMovement(userMovement);
                }
                if (Integer.parseInt(res.get(5))==1){
                    userMovement.setType("edit role:"+res.get(6).toString()+"-"+user.getRole());
                    userMovementController.CreateMovement(userMovement);
                }
                restart(username, name, password, role, search, edit, delete);
            }
        }
    }

    // check if the product is ok to be deleted from the DB
    public void deleteUser(JTextField username, JTextField name, JTextField password, JComboBox role, JButton search, JButton edit, JButton delete, User userMov){
        try {
            User user = new User();
            user.setUsername(username.getText());
            UserController userController = new UserController();
            userController.DeleteUser(id.toString());
            UserMovement userMovement = new UserMovement();
            // register the movement
            userMovement.setUsername(user.getUsername());
            UserMovementController userMovementController = new UserMovementController();
            userMovement.setUser(userMov.getUsername());
            userMovement.setType("delete");
            userMovementController.CreateMovement(userMovement);
            restart(username, name, password, role, search, edit, delete);
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, "El usuario no se eliminó " + err, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
