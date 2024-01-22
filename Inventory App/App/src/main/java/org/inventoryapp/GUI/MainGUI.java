package org.inventoryapp.GUI;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Classes.UserMovements.UserMovement;
import org.inventoryapp.Controllers.Main.MainController;
import org.inventoryapp.Controllers.UserMovement.UserMovementController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class MainGUI extends JFrame {

    private JPanel mainPanel;
    private JButton regOpen;
    private JButton productSearchBtn;
    private JTextField productSearch;
    private JTable products;
    private JPanel totalPanel;
    private JLabel total;
    private JScrollPane productsPanel;
    private JButton sell;
    private JButton inventory;
    private JTextField quantity;
    private JLabel id;
    private JLabel quantityLabel;
    private JButton users;
    private JButton regUser;

    MainController controller = new MainController();

    InventoryGUI inv;
    RegGUI rg;
    UsersGUI usersGUI;
    RegUserGUI regUserGUI;

    public MainGUI(User user) {
        setContentPane(mainPanel);
        setTitle("Administrador de inventario");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        products.getTableHeader().setReorderingAllowed(false);
        products.setColumnSelectionAllowed(true);
        controller.actTotal(products, total,productSearch);
        quantity.setText("1");

        controller.initialize(inventory, users,regUser,user);

        productSearchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.searchProducts(productSearch,products,total,quantity);
            }
        });

        productSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                controller.KeyboardController(products,e,total,productSearch,quantity);
            }
        });

        quantity.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                controller.KeyboardController(products,e,total,productSearch,quantity);
            }
        });

        products.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.MouseController(products,e,total,productSearch);
            }
        });

        products.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                controller.KeyboardController(products,e,total,productSearch,quantity);
            }
        });

        inventory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inv != null) {
                    if (!inv.isVisible()) {
                        inv = null;
                    }
                }
                if (inv == null) {
                    try {
                        inv = new InventoryGUI(user);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (FontFormatException ex) {
                        throw new RuntimeException(ex);
                    }
                    inv.setVisible(true);
                } else {
                    inv.toFront();
                }
            }
        });

        sell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.sellProducts(products,total,productSearch,user);
            }
        });

        regOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rg != null) {
                    if (!rg.isVisible()) {
                        rg = null;
                    }
                }
                if (rg == null) {
                    rg = new RegGUI();
                    rg.setVisible(true);
                } else {
                    rg.toFront();
                }
            }
        });

        users.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usersGUI != null) {
                    if (!usersGUI.isVisible()) {
                        usersGUI = null;
                    }
                }
                if (usersGUI == null) {
                    try {
                        usersGUI = new UsersGUI(user);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (FontFormatException ex) {
                        throw new RuntimeException(ex);
                    }
                    usersGUI.setVisible(true);
                } else {
                    usersGUI.toFront();
                }
            }
        });

        regUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (regUserGUI != null) {
                    if (!regUserGUI.isVisible()) {
                        regUserGUI = null;
                    }
                }
                if (regUserGUI == null) {
                    regUserGUI = new RegUserGUI();
                    regUserGUI.setVisible(true);
                } else {
                    regUserGUI.toFront();
                }
            }
        });

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                UserMovement userMovement = new UserMovement();
                userMovement.setUsername(user.getUsername());
                userMovement.setType("logout");
                UserMovementController userMovementController = new UserMovementController();
                userMovementController.CreateMovement(userMovement);
            }
        });
    }
}
