package org.inventoryapp.GUI;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import com.itextpdf.text.*;
import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Controllers.Inventory.InventoryController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InventoryGUI extends JFrame {
    private JLabel title;
    private JPanel inventoryPanel;
    private JScrollPane invGoodPane;
    private JTable GoodInventory;
    private JButton addProduct;
    private JButton editProduct;
    private JTable MediumInventory;
    private JScrollPane invMedPane;
    private JTable BadInventory;
    private JScrollPane invBadPane;
    private JLabel tb1;
    private JLabel tb2;
    private JLabel tb3;
    private JButton act;
    private JButton download;

    InventoryController controller = new InventoryController();

    EditProductGUI ep;
    AddProductGUI pd;

    public InventoryGUI(User user) throws IOException, FontFormatException {
        setContentPane(inventoryPanel);
        setTitle("Inventario");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        GoodInventory.getTableHeader().setReorderingAllowed(false);
        GoodInventory.setColumnSelectionAllowed(true);
        MediumInventory.getTableHeader().setReorderingAllowed(false);
        MediumInventory.setColumnSelectionAllowed(true);
        BadInventory.getTableHeader().setReorderingAllowed(false);
        BadInventory.setColumnSelectionAllowed(true);
        controller.showInventory(GoodInventory,MediumInventory,BadInventory);

        editProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ep != null) {
                    if (!ep.isVisible()) {
                        ep = null;
                    }
                }
                if (ep == null) {
                    ep = new EditProductGUI(user);
                    ep.setVisible(true);
                } else {
                    ep.toFront();
                }
            }
        });

        addProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pd != null) {
                    if (!pd.isVisible()) {
                        pd = null;
                    }
                }
                if (pd == null) {
                    pd = new AddProductGUI(user);
                    pd.setVisible(true);
                } else {
                    pd.toFront();
                }
            }
        });

        act.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.showInventory(GoodInventory,MediumInventory,BadInventory);
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
                        controller.downloadPdf(GoodInventory,MediumInventory,BadInventory);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null,"Debes cerrar el inventario que tienes abierto de hoy","Error",JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,"No se guardó el archivo "+ex,"Error",JOptionPane.ERROR_MESSAGE);
                    } catch (DocumentException ex) {
                        JOptionPane.showMessageDialog(null,"No se guardó el archivo "+ex,"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }else if(type == JOptionPane.NO_OPTION) {
                    try {
                        controller.downloadExcel(GoodInventory,MediumInventory,BadInventory);
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(null,"Debes cerrar el inventario que tienes abierto de hoy","Error",JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null,"No se guardó el archivo "+ex,"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
}
