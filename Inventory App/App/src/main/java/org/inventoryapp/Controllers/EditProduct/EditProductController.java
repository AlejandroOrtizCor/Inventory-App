package org.inventoryapp.Controllers.EditProduct;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import org.inventoryapp.Classes.Movements.Movement;
import org.inventoryapp.Classes.Product.Product;
import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Controllers.Movements.MovementController;
import org.inventoryapp.Controllers.Product.ProductController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class EditProductController {
    // prefix for custom products
    String prefix = "";

    // restart the gui
    public void restart(JTextField id,JTextField name, JTextField price, JTextField quantity, JButton search, JButton edit, JButton delete) {
        id.setText(null);
        name.setText(null);
        price.setText(null);
        quantity.setText(null);

        id.setEnabled(true);
        search.setText("Buscar");
        name.setEnabled(false);
        price.setEnabled(false);
        quantity.setEnabled(false);
        edit.setEnabled(false);
        delete.setEnabled(false);
    }

    // search the product to be edited
    public void searchProducts(JTextField id,JTextField name, JTextField price, JTextField quantity, JButton search, JButton edit, JButton delete) {
        String Id = id.getText();
        // variable if the query returns more than 1 product
        boolean nonselected=false;
        // put prefix for the barcode for the custom products
        if (Id.startsWith("00") && Id.length()<=3) {
            Id = Id.replaceFirst("00", "");
            prefix = "00";
        } else if (Id.startsWith("0") && Id.length()<=3) {
            Id = Id.replaceFirst("0", "");
            prefix = "0";
        }
        ArrayList<Product> productList;
        Product product = new Product();
        ProductController productController = new ProductController();
        // check if entry is the product's id or name
        try {
            Long.parseLong(Id);

            productList = productController.SearchProduct(Id, null);
        } catch (Exception err) {
            productList = productController.SearchProduct(null, Id.toLowerCase());
        }
        // check if query returns 1 product or more
        if (productList.size()==1){
            product = productList.get(0);
        }else if(productList.size()>1) {
            if (Id.length()==1) {
                prefix = "00";
            } else if (Id.length()==2) {
                prefix = "0";
            } else {
                prefix = "0";
            }
            // create frame to select the product from the product list
            Object[][] rows = new Object[productList.size()][3];
            for (int i = 0; i < productList.size(); i++) {
                Object[] row = {productList.get(i).getId(), productList.get(i).getName() + " (Existencias: " + productList.get(i).getQuantity().toString() + ")", productList.get(i).getPrice()};
                rows[i] = row;
            }
            Object[] cols = {"Codigo", "Nombre", "Precio"};
            JTable possibleProduct = new JTable();
            DefaultTableModel model = new DefaultTableModel(rows, cols) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            possibleProduct.setModel(model);
            JTextField index = new JTextField();
            JPanel panel = new JPanel();
            JScrollPane pane = new JScrollPane(possibleProduct);
            BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
            index.setVisible(false);
            panel.setLayout(layout);
            panel.add(pane);
            panel.add(index);
            possibleProduct.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    index.setText(String.valueOf(possibleProduct.getSelectedRow()));
                    JOptionPane.getRootFrame().dispose();
                }
            });
            JOptionPane.showOptionDialog(null, panel, "Productos coincidentes", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
            // get selected product
            try {
                product = productList.get(Integer.parseInt(index.getText()));
            } catch (Exception err) {
                nonselected = true;
            }
        }
        // if the product is found fills and enables the text fields
        if (product.getName() != null) {
            id.setText(prefix+product.getId().toString());
            name.setText(product.getName());
            price.setText(product.getPrice().toString());
            quantity.setText(product.getQuantity().toString());

            id.setEnabled(false);
            search.setText("Nueva Busqueda");
            name.setEnabled(true);
            price.setEnabled(true);
            quantity.setEnabled(true);
            edit.setEnabled(true);
            delete.setEnabled(true);
        } else if(nonselected==false) {
            JOptionPane.showMessageDialog(null, "No se encuentra el producto", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // check if the product is ok to be edited on the DB
    public void editProduct(JTextField id, JTextField name, JTextField price, JTextField quantity, User user) {
        String Name = name.getText().toLowerCase();
        // check if quantity is not numeric
        try {
            Integer.parseInt(quantity.getText());
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Las existencias son invalidas", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer Quantity = Integer.parseInt(quantity.getText());
        // check if name is numeric
        try{
            Long.parseLong(name.getText());
            JOptionPane.showMessageDialog(null, "No se puede poner un nombre que sean solo numeros, debes combinarlo con letras", "Error", JOptionPane.ERROR_MESSAGE);
        }catch (Exception e) {
            // check if there is empty spaces
            if (Name.isEmpty() || price.getText().isEmpty() || quantity.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Tienes campos en blanco", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (Quantity < 0) {
                JOptionPane.showMessageDialog(null, "No se puede poner existencias negativas", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    Integer Price = Integer.parseInt(price.getText());
                    if (Price > 0) {
                        // variable to identify the product changes
                        ArrayList<String> res;
                        Product product = new Product();
                        // create product
                        product.setName(Name);
                        product.setPrice(Price);
                        product.setQuantity(Quantity);
                        ProductController productController = new ProductController();
                        res = productController.EditProduct(id.getText(), product);
                        // register the movement if product is correctly edited
                        Movement movement = new Movement();
                        movement.setProductId(Long.parseLong(id.getText()));
                        movement.setProductName(Name);
                        movement.setUsername(user.getUsername());
                        MovementController movementController = new MovementController();
                        // create the type of movement according to the done action
                        if (Integer.parseInt(res.get(2))==1){
                            movement.setType("name change: "+res.get(3)+"-"+Name);
                            movementController.CreateMovement(movement);
                        }
                        if (Integer.parseInt(res.get(0))>0) {
                            movement.setType("out: "+res.get(0).toString());
                            movementController.CreateMovement(movement);
                        }else if (Integer.parseInt(res.get(0))<0){
                            movement.setType("in: "+res.get(0).toString());
                            movementController.CreateMovement(movement);
                        }
                        if (Integer.parseInt(res.get(1))>0) {
                            movement.setType("down: "+res.get(1).toString());
                            movementController.CreateMovement(movement);
                        }else if (Integer.parseInt(res.get(1))<0){
                            movement.setType("up: "+res.get(1).toString());
                            movementController.CreateMovement(movement);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El precio no puede ser negativo ni ser 0", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException err) {
                    JOptionPane.showMessageDialog(null, "El precio tiene caracteres invalidos, recuerda no poner puntos ni comas", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(null, "El producto no se editó correctamente " + err, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // check if the product is ok to be deleted from the DB
    public void deleteProduct(JTextField id,JTextField name, JTextField price, JTextField quantity, JButton search, JButton edit, JButton delete, User user) {
        try {
            String Name = name.getText().toLowerCase();
            ProductController productController = new ProductController();
            productController.DeleteProduct(id.getText());
            Movement movement = new Movement();
            // register the movement
            movement.setProductId(Long.parseLong(id.getText()));
            movement.setProductName(Name);
            movement.setUsername(user.getUsername());
            movement.setType("delete");
            MovementController movementController = new MovementController();
            movementController.CreateMovement(movement);
            restart(id,name,price,quantity,search,edit,delete);
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, "El producto no se eliminó " + err, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
