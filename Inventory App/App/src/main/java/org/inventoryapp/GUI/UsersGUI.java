package org.inventoryapp.GUI;

import com.itextpdf.text.DocumentException;
import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Controllers.Users.UsersController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UsersGUI extends JFrame{
    private JPanel usersPanel;
    private JLabel title;
    private JTable users;
    private JScrollPane userPanel;
    private JButton addUser;
    private JButton act;
    private JButton download;
    private JButton editUser;

    UsersController usersController = new UsersController();

    EditUserGUI editUserGUI;
    AddUserGUI addUserGUI;

    public UsersGUI (User user) throws IOException, FontFormatException {
        setContentPane(usersPanel);
        setTitle("Usuarios");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        users.getTableHeader().setReorderingAllowed(false);
        users.setColumnSelectionAllowed(true);
        usersController.showUsers(users);

        editUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editUserGUI != null) {
                    if (!editUserGUI.isVisible()) {
                        editUserGUI = null;
                    }
                }
                if (editUserGUI == null) {
                    editUserGUI = new EditUserGUI(user);
                    editUserGUI.setVisible(true);
                } else {
                    editUserGUI.toFront();
                }
            }
        });

        addUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addUserGUI != null) {
                    if (!addUserGUI.isVisible()) {
                        addUserGUI = null;
                    }
                }
                if (addUserGUI == null) {
                    addUserGUI = new AddUserGUI(user);
                    addUserGUI.setVisible(true);
                } else {
                    addUserGUI.toFront();
                }
            }
        });

        act.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    usersController.showUsers(users);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (FontFormatException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Pdf","Excel"};
                int type = JOptionPane.showOptionDialog(null,"¿Deseas descargar el archivo como Pdf o como Excel?","Descarga",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,null);
                if (type == JOptionPane.YES_OPTION){
                    try {
                        usersController.downloadPdf(users);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null,"Debes cerrar el reporte que tienes abierto de hoy","Error",JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,"No se guardó el archivo "+ex,"Error",JOptionPane.ERROR_MESSAGE);
                    } catch (DocumentException ex) {
                        JOptionPane.showMessageDialog(null,"No se guardó el archivo "+ex,"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }else if(type == JOptionPane.NO_OPTION) {
                    try {
                        usersController.downloadExcel(users);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null,"Debes cerrar el reporte que tienes abierto de hoy","Error",JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,"No se guardó el archivo "+ex,"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
}
