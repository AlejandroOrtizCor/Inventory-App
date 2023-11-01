package org.inventoryapp.GUI;

import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Controllers.AddUser.AddUserController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AddUserGUI extends JFrame{
    private JPanel userPanel;
    private JLabel title;
    private JLabel nameLabel;
    private JTextField name;
    private JLabel usernameLabel;
    private JTextField username;
    private JTextField password;
    private JLabel passwordLabel;
    private JComboBox role;
    private JLabel roleLabel;
    private JButton save;

    AddUserController addUserController = new AddUserController();

    public AddUserGUI(User user) {
        setContentPane(userPanel);
        setTitle("Registrar Usuario");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        role.addItem("Empleado");
        role.addItem("Administrador");

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUserController.saveUser(name,username,password,role,user);
            }
        });

        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String[] words = name.getText().toLowerCase().split(" ");
                if (words.length==2 && words[0].length()>=2){
                    String uname = words[0].substring(0,2)+words[1];
                    username.setText(uname);
                }
                if (words.length>2 && words[2].length()>=2){
                    String uname = words[0].substring(0,2)+words[1]+words[2].substring(0,2);
                    username.setText(uname);
                }
                if (words.length<2){
                    username.setText("Debes poner al menos un nombre y un apellido");
                }
            }
        });
        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    addUserController.saveUser(name,username,password,role,user);
                }
            }
        });
        password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    addUserController.saveUser(name,username,password,role,user);
                }
            }
        });
    }
}
