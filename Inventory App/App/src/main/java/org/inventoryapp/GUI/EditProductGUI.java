package org.inventoryapp.GUI;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Controllers.EditProduct.EditProductController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EditProductGUI extends JFrame{
    private JLabel title;
    private JTextField id;
    private JTextField name;
    private JTextField price;
    private JTextField quantity;
    private JButton edit;
    private JButton search;
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel quantityLabel;
    private JPanel editPanel;
    private JButton delete;

    EditProductController controller = new EditProductController();
    
    public EditProductGUI (User user) {
        setContentPane(editPanel);
        setTitle("Editar Producto");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (search.getText() == "Buscar") {
                    controller.searchProducts(id,name,price,quantity,search,edit,delete);
                } else {
                    controller.restart(id,name,price,quantity,search,edit,delete);
                }
            }
        });
        id.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    controller.searchProducts(id,name,price,quantity,search,edit,delete);
                }
            }
        });
        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    controller.editProduct(id,name,price,quantity,user);
                }
            }
        });
        price.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    controller.editProduct(id,name,price,quantity,user);
                }
            }
        });
        quantity.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    controller.editProduct(id,name,price,quantity,user);
                }
            }
        });
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.editProduct(id,name,price,quantity,user);
            }
        });
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.deleteProduct(id,name,price,quantity,search,edit,delete,user);
            }
        });
    }
}
