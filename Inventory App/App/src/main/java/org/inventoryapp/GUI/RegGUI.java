package org.inventoryapp.GUI;

import com.itextpdf.text.DocumentException;
import com.toedter.calendar.JTextFieldDateEditor;
import org.inventoryapp.Controllers.Reg.RegController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class RegGUI extends JFrame {
    private JPanel regPanel;
    private JLabel title;
    private JTable regTable;
    private JButton download;
    private JPanel datePanel;
    private JButton search;
    private JLabel idLabel;
    private JLabel dateLabel;
    private JTextField id;
    private JScrollPane reg;
    private JComboBox filter;
    private JLabel filterLabel;
    private JTextField username;
    private JLabel usernameLabel;
    private com.toedter.calendar.JDateChooser dateChooser;

    RegController controller = new RegController();

    private void Initialize() {
        filter.addItem("Ninguno");
        filter.addItem("Entradas");
        filter.addItem("Salidas");
        filter.addItem("Agotamiento de exitencias");
        filter.addItem("Edición de producto");
        filter.addItem("Creación de producto");
        filter.addItem("Eliminación de producto");

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

    public RegGUI () {
        setContentPane(regPanel);
        setTitle("Registro Movimientos");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        regTable.getTableHeader().setReorderingAllowed(false);
        regTable.setColumnSelectionAllowed(true);
        Initialize();

        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.MainController(id,dateChooser,regTable,filter,username);
            }
        });
        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Pdf","Excel"};
                int type = JOptionPane.showOptionDialog(null,"¿Deseas descargar el archivo como Pdf o como Excel?","Descarga",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,null);
                if (type == JOptionPane.YES_OPTION){
                    try {
                        controller.downloadPdf(regTable);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null,"Debes cerrar el reporte que tienes abierto de hoy","Error",JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (DocumentException ex) {
                        throw new RuntimeException(ex);
                    }
                }else if(type == JOptionPane.NO_OPTION) {
                    try {
                        controller.downloadExcel(regTable);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null,"Debes cerrar el reporte que tienes abierto de hoy","Error",JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        id.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    controller.MainController(id,dateChooser,regTable,filter,username);
                }
            }
        });
        username.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    controller.MainController(id,dateChooser,regTable,filter,username);
                }
            }
        });
    }
}
