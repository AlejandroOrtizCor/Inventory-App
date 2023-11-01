package org.inventoryapp.GUI;

import org.inventoryapp.Controllers.Login.LoginController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class LoginGUI extends JFrame{
    private JButton login;
    private JLabel title;
    private JLabel usernameLabel;
    private JTextField username;
    private JPasswordField password;
    private JLabel passwordLabel;
    private JPanel loginPanel;
    private JPanel innerPanel;

    LoginController loginController = new LoginController();

    public LoginGUI() {
        setContentPane(loginPanel);
        setTitle(null);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
        setIconImage(new ImageIcon(icon).getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        username.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
        username.setToolTipText(null);
        password.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
        password.setToolTipText(null);
        login.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getForeground()));
        innerPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray));

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginController.login(username,password,LoginGUI.this);
            }
        });
        username.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    loginController.login(username,password,LoginGUI.this);
                }
            }
        });
        password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    loginController.login(username,password,LoginGUI.this);
                }
            }
        });
    }
}
