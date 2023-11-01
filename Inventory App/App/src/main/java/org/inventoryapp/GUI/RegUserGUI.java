package org.inventoryapp.GUI;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import com.itextpdf.text.DocumentException;
import com.toedter.calendar.JTextFieldDateEditor;
import org.inventoryapp.Controllers.RegUser.RegUserController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class RegUserGUI extends JFrame {
    private JPanel regUserPanel;
    private JLabel title;
    private JLabel dateLabel;
    private JLabel usernameLabel;
    private JPanel datePanel;
    private JTextField username;
    private JLabel filterLabel;
    private JComboBox filter;
    private JButton search;
    private JScrollPane reg;
    private JTable regTable;
    private JButton download;
    private com.toedter.calendar.JDateChooser dateChooser;

    RegUserController regUserController = new RegUserController();

    private void Initialize() {
        filter.addItem("Ninguno");
        filter.addItem("Conexión");
        filter.addItem("Desconexión");
        filter.addItem("Creación de usuario");
        filter.addItem("Eliminación de usuario");
        filter.addItem("Edición de usuario");

        dateChooser = new com.toedter.calendar.JDateChooser();
        dateChooser.setDate(Date.valueOf(LocalDate.now()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(datePanel);
        datePanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        JTextFieldDateEditor dateEditor = (JTextFieldDateEditor) dateChooser.getDateEditor();
        dateEditor.setHorizontalAlignment(JTextField.CENTER);
    }

    public RegUserGUI () {
        setContentPane(regUserPanel);
        setTitle("Registro Movimientos Usuarios");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        regTable.getTableHeader().setReorderingAllowed(false);
        regTable.setColumnSelectionAllowed(true);
        Initialize();

        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                regUserController.MainController(username,dateChooser,regTable,filter);
            }
        });
        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Pdf","Excel"};
                int type = JOptionPane.showOptionDialog(null,"¿Deseas descargar el archivo como Pdf o como Excel?","Descarga",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,null);
                if (type == JOptionPane.YES_OPTION){
                    try {
                        regUserController.downloadPdf(regTable);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null,"Debes cerrar el reporte que tienes abierto de hoy","Error",JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (DocumentException ex) {
                        throw new RuntimeException(ex);
                    }
                }else if(type == JOptionPane.NO_OPTION) {
                    try {
                        regUserController.downloadExcel(regTable);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null,"Debes cerrar el reporte que tienes abierto de hoy","Error",JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        username.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    regUserController.MainController(username,dateChooser,regTable,filter);
                }
            }
        });
    }
}
