package org.inventoryapp.Controllers.AddUser;

import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Classes.UserMovements.UserMovement;
import org.inventoryapp.Controllers.User.UserController;
import org.inventoryapp.Controllers.UserMovement.UserMovementController;

import javax.swing.*;

public class AddUserController {
    // restart the gui
    public void restart(JTextField name, JTextField username, JTextField password, JComboBox role){
        name.setText("");
        username.setText("");
        password.setText("");
        role.setSelectedIndex(0);
    }

    // check if the user is ok to be uploaded to DB
    public void saveUser(JTextField name, JTextField username, JTextField password, JComboBox role, User userMov) {
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
            UserController userController = new UserController();
            boolean invalid = userController.CreateUser(user);
            // register the movement if user is correctly created
            if (invalid == false) {
                UserMovement userMovement = new UserMovement();
                userMovement.setUsername(user.getUsername());
                userMovement.setType("create");
                userMovement.setUser(userMov.getUsername());
                UserMovementController userMovementController = new UserMovementController();
                userMovementController.CreateMovement(userMovement);
                restart(name, username, password, role);
            }
        }
    }
}
