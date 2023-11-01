package org.inventoryapp.Controllers.Main;

import org.inventoryapp.Classes.Movements.Movement;
import org.inventoryapp.Classes.Product.Product;
import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Controllers.Movements.MovementController;
import org.inventoryapp.Controllers.Product.ProductController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.ArrayList;

public class MainController {
    // make global list for products to sell
    ArrayList<Product> productsList = new ArrayList<Product>();;

    // global variable of prefix for custom products
    String prefix = "";

    // check if the current user is an employee or admin to hide the admin components
    public void initialize(JButton inventory, JButton addUser, JButton regUser, User user){
        if (user.getRole().equals("Employee")){
            inventory.setVisible(false);
            addUser.setVisible(false);
            regUser.setVisible(false);
        }
    }

    // function to fill the table with products to sell
    public DefaultTableModel fillTable(ArrayList<Product> product) {
        // make the table non-editable
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // create columns
        model.addColumn("Codigo");
        model.addColumn("Nombre");
        model.addColumn("Precio");

        // read all products
        for (int i = 0; i < product.size(); i++) {
            if (product.get(i).getId().toString().length()==1){
                prefix = "00";
            } else if (product.get(i).getId().toString().length()==2){
                prefix = "0";
            } else {
                prefix = "";
            }
            // create a table row and adds it to the table
            String[] data = new String[]{prefix + product.get(i).getId().toString(), product.get(i).getName() + " (Existencias: " + product.get(i).getQuantity() + ")", product.get(i).getPrice().toString()};
            model.addRow(data);
        }
        return model;
    }

    // detects products to sell and make the changes in quantity
    public void sellProducts(JTable products, JLabel total,JTextField productSearch, User user) {
        // lists to count and detect the products to sell
        ArrayList<Long> indexes = new ArrayList<Long>();
        ArrayList<Integer> contT = new ArrayList<Integer>();
        ArrayList<Product> productsListT = new ArrayList<Product>();

        // a new label with the total text for the emergent window
        JLabel totalT = new JLabel(total.getText());
        totalT.setFont(total.getFont());

        // detects if there aren't products to sell
        if (Integer.parseInt(totalT.getText().replace("$","").replace(".","")) == 0){
            JOptionPane.showMessageDialog(null,"No hay ningun producto para vender","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        // creates the emergent window to check the change money of the sell
        JPanel panel = new JPanel();
        JTextField received = new JTextField(totalT.getText().replace("$","").replace(".",""));
        JTextField change = new JTextField("$0");
        JLabel t1 = new JLabel("Total a pagar:");
        JLabel t2 = new JLabel("Recibido:");
        JLabel t3 = new JLabel("Cambio:");
        change.setEditable(false);
        BoxLayout layout = new BoxLayout(panel,BoxLayout.PAGE_AXIS);
        panel.setLayout(layout);
        panel.add(t1);
        panel.add(totalT);
        panel.add(t2);
        panel.add(received);
        panel.add(t3);
        panel.add(change);

        // actualizes the change field according to the received money
        received.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!received.getText().isEmpty()){
                    try {
                        Long receivedMoney = Long.parseLong(received.getText());
                        Long totalToPay = Long.parseLong(totalT.getText().replace("$","").replace(".",""));
                        Long changeMoney = (totalToPay-receivedMoney)*-1;
                        if (changeMoney>=0) {
                            change.setText("$" + changeMoney.toString().format("%,d", changeMoney));
                        }else{
                            change.setText("Digita un valor de dinero recibido valido");
                        }
                    }catch (NumberFormatException err){
                        change.setText("Digita un valor de dinero recibido valido");
                    }
                }else{
                    change.setText("Digita un valor de dinero recibido valido");
                }
            }
        });
        // shows the emergent window
        Object[] options = {"Cancelar venta","Confirmar venta"};
        int type = JOptionPane.showOptionDialog(null, panel, "Completar venta", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, "Confirmar venta");
        // if the sell is confirmed makes the change on DB
        if (type == JOptionPane.NO_OPTION) {
            // fills lists with the non-repeated products and change them the quantity according to the sell
            for (int i = 0; i < productsList.size(); i++) {
                if (!indexes.contains(productsList.get(i).getId())){
                    indexes.add(productsList.get(i).getId());
                }
            }
            for (int i = 0; i < indexes.size(); i++) {
                boolean exist = false;
                for (int j = 0; j < productsList.size(); j++) {
                    if (productsList.get(j).getId() == indexes.get(i) && exist == false){
                        productsListT.add(productsList.get(j));
                        exist = true;
                    }
                }
            }
            for (int i = 0; i < productsListT.size(); i++) {
                Integer cont = 0;
                for (int j = 0; j < productsList.size(); j++) {
                    if (productsListT.get(i).getId().equals(productsList.get(j).getId())){
                        cont+=1;
                    }
                }
                contT.add(cont);
                productsListT.get(i).setQuantity(productsListT.get(i).getQuantity()-cont);
            }
            // makes the change on DB
            ProductController productController = new ProductController();
            productController.SellProducts(productsListT);
            // register the movement
            Movement movement = new Movement();
            MovementController movementController = new MovementController();
            for (int i = 0; i < productsListT.size(); i++) {
                if (productsListT.get(i).getQuantity()==0){
                    movement.setProductId(productsListT.get(i).getId());
                    movement.setProductName(productsListT.get(i).getName());
                    movement.setType("sell - out of stock: "+contT.get(i));
                    movement.setUsername(user.getUsername());
                    movementController.CreateMovement(movement);
                } else {
                    movement.setProductId(productsListT.get(i).getId());
                    movement.setProductName(productsListT.get(i).getName());
                    movement.setType("sell: "+contT.get(i));
                    movement.setUsername(user.getUsername());
                    movementController.CreateMovement(movement);
                }
            }
            // clears GUI
            productsList.clear();
            products.setModel(fillTable(productsList));
            actTotal(products, total,productSearch);
        }
    }

    // actualizes the total price of the products to sell
    public void actTotal(JTable products,JLabel total,JTextField productSearch) {
        Integer cont = 0;
        productSearch.setText("");
        for (int i = 0; i < products.getModel().getRowCount(); i++) {
            cont += Integer.parseInt(products.getModel().getValueAt(i, 2).toString());
        }
        total.setText("$" + cont.toString().format("%,d", cont));
    }

    // searches the products on DB
    public void searchProducts(JTextField productSearch, JTable products, JLabel total, JTextField quantity) {
        boolean nonselected = false;
        String id = productSearch.getText();
        // removes prefix if it's a custom product
        if (id.startsWith("00")) {
            id = id.replaceFirst("00", "");
            prefix = "00";
        } else if (id.startsWith("0")) {
            id = id.replaceFirst("0", "");
            prefix = "0";
        }

        // create a list of products if the searching returns more than one product
        ArrayList<Product> productList;
        Product product = new Product();
        ProductController productController = new ProductController();

        // check if the id field is empty
        if (id.isEmpty()){
            JOptionPane.showMessageDialog(null,"Digita el codigo o el nombre del producto","Error",JOptionPane.ERROR_MESSAGE);
        } else {
            // check if the id field contains only numbers or not
            try {
                Long.parseLong(id);

                // if it's numeric the products will be searched by id on the DB
                productList = productController.SearchProduct(id, null);
            } catch (Exception err) {
                // if it's not numeric the products will be searched by name on the DB
                productList = productController.SearchProduct(null, id.toLowerCase());
            }
            // if the query returns one product it will be added to the global list
            if (productList.size()==1){
                product = productList.get(0);
            // if the query returns more than one product it will show an emergent window to make the user decide for only one product
            }else if(productList.size()>1){
                // makes an array for the table rows to select the product
                Object[][] rows = new Object[productList.size()][3];
                for (int i = 0; i < productList.size(); i++) {
                    Object[] row = {productList.get(i).getId(),productList.get(i).getName()+" (Existencias: "+productList.get(i).getQuantity().toString()+")",productList.get(i).getPrice()};
                    rows[i] = row;
                }
                Object[] cols = {"Codigo","Nombre","Precio"};
                // creates the table
                JTable possibleProduct = new JTable();
                // make the table non-editable and fill it
                DefaultTableModel model = new DefaultTableModel(rows,cols) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                possibleProduct.setModel(model);
                JTextField index = new JTextField();
                JPanel panel = new JPanel();
                JScrollPane pane = new JScrollPane(possibleProduct);
                BoxLayout layout = new BoxLayout(panel,BoxLayout.Y_AXIS);
                index.setVisible(false);
                panel.setLayout(layout);
                panel.add(pane);
                panel.add(index);
                // check what product is clicked to select that one
                possibleProduct.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        index.setText(String.valueOf(possibleProduct.getSelectedRow()));
                        JOptionPane.getRootFrame().dispose();
                    }
                });
                // shows the emergent window
                JOptionPane.showOptionDialog(null, panel,"Productos coincidentes",JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null,new Object[]{},null);
                try {
                    // add to global list the selected product
                    product = productList.get(Integer.parseInt(index.getText()));
                } catch (Exception err){
                    // if the user closes the emergent window it won't add any product
                    nonselected=true;
                }
            }
            // check if the return of the searching on DB is empty
            if (product.getName() != null) {
                // check if the product is out of stock
                if (product.getQuantity() > 0) {
                    Integer cont = 0;
                    Integer quant;
                    try {
                        // checks if the products added to sell doesn't overcome the current quantity of the product
                        quant = Integer.parseInt(quantity.getText());
                        for (int i = 0; i < productsList.size(); i++) {
                            if (productsList.get(i).getId() == product.getId()) {
                                cont += 1;
                            }
                        }
                        if (cont + quant-1 >= product.getQuantity()) {
                            JOptionPane.showMessageDialog(null, "El producto no tiene suficientes existencias", "Sin existencias", JOptionPane.ERROR_MESSAGE);
                            quantity.setText("1");
                        } else {
                            // checks if user didn't put a invalid quantity
                            if (quant<=0){
                                JOptionPane.showMessageDialog(null, "La cantidad no debe ser menor o igual a 0", "Error", JOptionPane.ERROR_MESSAGE);
                            }else {
                                // add the products to sell to the global list
                                if (quant == 1) {
                                    productsList.add(product);
                                } else {
                                    for (int i = 0; i < quant; i++) {
                                        productsList.add(product);
                                    }
                                }
                                quantity.setText("1");
                                products.setModel(fillTable(productsList));
                            }
                        }
                    } catch (Exception e){
                        JOptionPane.showMessageDialog(null, "La cantidad debe ser un numero", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "El producto no tiene existencias", "Sin existencias", JOptionPane.ERROR_MESSAGE);
                }
            } else if (nonselected==false){
                JOptionPane.showMessageDialog(null, "No se encuentra el producto", "Error", JOptionPane.ERROR_MESSAGE);
            }
            actTotal(products, total,productSearch);
        }
    }

    // controllers for the fields
    public void KeyboardController(JTable products, KeyEvent e, JLabel total, JTextField productSearch, JTextField quantity) {
        // if user press escape the products to sell will be all deleted from the table
        if (e.getKeyCode() == 27) {
            productsList.clear();
            products.setModel(fillTable(productsList));
            actTotal(products, total,productSearch);
        }
        // if user press enter on the field it will search the product
        if (e.getKeyCode() == 10) {
            searchProducts(productSearch,products,total,quantity);
        }
        // if user press ctrl+z the last added product will be deleted from the table
        if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            productsList.remove(productsList.size()-1);
            products.setModel(fillTable(productsList));
            actTotal(products,total,productSearch);
        }
    }

    // controller for the table
    public void MouseController(JTable products, MouseEvent e, JLabel total,JTextField productSearch) {
        // if the user ctrl+click a product to sell already added it will be deleted from the table
        if (e.isControlDown() && e.getButton() == 1 && products.getSelectedRow()!=-1) {
            productsList.remove(products.getSelectedRow());
            products.setModel(fillTable(productsList));
            actTotal(products,total,productSearch);
        }
    }
}
