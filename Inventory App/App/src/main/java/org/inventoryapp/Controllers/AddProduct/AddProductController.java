package org.inventoryapp.Controllers.AddProduct;

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

import static java.lang.Long.parseLong;

public class AddProductController {
    // restart the gui
    public void restart(JTextField name,JTextField quantity,JTextField price,JTextField codebar) {
        codebar.setText("");
        name.setText("");
        quantity.setText("");
        price.setText("");
    }

    // check if the product is ok to be uploaded to DB
    public void saveProduct(JTextField name, JTextField quantity, JTextField price, JTextField codebar, User user) {
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
            Integer.parseInt(name.getText());
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
                        Product product = new Product();
                        // check if the product is one with existing barcode
                        if (!codebar.getText().isEmpty()){
                            product.setId(parseLong(codebar.getText()));
                        }
                        // create product
                        product.setName(Name);
                        product.setPrice(Price);
                        product.setQuantity(Quantity);
                        ProductController productController = new ProductController();
                        product = productController.CreateProduct(product);
                        // register the movement if product is correctly created
                        if (product.getId() != null) {
                            Movement movement = new Movement();
                            movement.setProductId(product.getId());
                            movement.setProductName(Name);
                            movement.setType("create");
                            movement.setUsername(user.getUsername());
                            MovementController movementController = new MovementController();
                            movementController.CreateMovement(movement);
                            restart(name,quantity,price,codebar);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El precio no puede ser negativo ni ser 0", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException err) {
                    JOptionPane.showMessageDialog(null, "El precio tiene caracteres invalidos, recuerda no poner puntos ni comas", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(null, "El producto no se guardó " + err, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
