package org.inventoryapp.GUI;

import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Controllers.AddProduct.AddProductController;

import javax.swing.*;
import java.awt.event.*;

public class AddProductGUI extends JFrame {

    private JTextField name;
    private JTextField quantity;
    private JLabel title;
    private JLabel priceLabel;
    private JLabel nameLabel;
    private JLabel quantityLabel;
    private JButton guardarProductoButton;
    private JPanel productPanel;
    private JTextField price;
    private JLabel codebarLabel;
    private JTextField codebar;

    AddProductController controller = new AddProductController();

    public AddProductGUI(User user) {
        setContentPane(productPanel);
        setTitle("Agregar Producto");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        guardarProductoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.saveProduct(name,quantity,price,codebar,user);
            }
        });

        codebar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    controller.saveProduct(name,quantity,price,codebar,user);
                }
            }
        });

        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    controller.saveProduct(name,quantity,price,codebar,user);
                }
            }
        });

        price.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    controller.saveProduct(name,quantity,price,codebar,user);
                }
            }
        });

        quantity.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    controller.saveProduct(name,quantity,price,codebar,user);
                }
            }
        });
    }

}
