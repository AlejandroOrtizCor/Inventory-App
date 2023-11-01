package org.inventoryapp.GUI;

import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Controllers.EditUser.EditUserController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EditUserGUI extends JFrame{
    private JPanel editPanel;
    private JLabel title;
    private JTextField name;
    private JTextField username;
    private JTextField password;
    private JComboBox role;
    private JButton edit;
    private JLabel nameLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel roleLabel;
    private JButton search;
    private JButton delete;

    EditUserController editUserController = new EditUserController();

    public EditUserGUI(User user) {
        setContentPane(editPanel);
        setTitle("Editar Usuario");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        role.addItem("Empleado");
        role.addItem("Administrador");

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
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (search.getText()=="Buscar"){
                    editUserController.searchUser(username,name,password,role,search,edit,delete,user);
                }else{
                    editUserController.restart(username,name,password,role,search,edit,delete);
                }
            }
        });
        username.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    editUserController.searchUser(username,name,password,role,search,edit,delete,user);
                }
            }
        });
        password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    editUserController.editUser(username,name,password,role,search,edit,delete,user);
                }
            }
        });
        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    editUserController.editUser(username,name,password,role,search,edit,delete,user);
                }
            }
        });
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUserController.editUser(username,name,password,role,search,edit,delete,user);
            }
        });
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUserController.deleteUser(username,name,password,role,search,edit,delete,user);
            }
        });
    }
}
