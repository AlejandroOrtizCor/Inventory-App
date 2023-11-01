package org.inventoryapp.Controllers.Login;

import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Classes.UserMovements.UserMovement;
import org.inventoryapp.Controllers.User.UserController;
import org.inventoryapp.Controllers.UserMovement.UserMovementController;
import org.inventoryapp.GUI.MainGUI;

import javax.swing.*;

public class LoginController {
    // searches the user given to check if is in the DB and its credentials
    public void login(JTextField username, JPasswordField password, JFrame loginFrame) {
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tienes campos en blanco", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        UserController userController = new UserController();
        User user = userController.SearchByUsername(username.getText());
        if (user.getUsername() == null){
            JOptionPane.showMessageDialog(null, "Usuario no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
        }else if (!user.getPassword().equals(password.getText())){
            JOptionPane.showMessageDialog(null, "Contraseña incorrecta", "Error", JOptionPane.ERROR_MESSAGE);
        }else {
            // register the login movement to DB
            UserMovement userMovement = new UserMovement();
            userMovement.setUsername(user.getUsername());
            userMovement.setType("login");
            UserMovementController userMovementController = new UserMovementController();
            userMovementController.CreateMovement(userMovement);
            loginFrame.setVisible(false);
            MainGUI main = new MainGUI(user);
            main.setVisible(true);
        }
    }
}
