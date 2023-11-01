package org.inventoryapp.Controllers.Product;

import org.inventoryapp.Classes.Product.Product;
import org.inventoryapp.SQL.ConnectionSQL;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ProductController {
    // global variables to make queries
    Statement st;
    ResultSet rs;
    String query;

    // creates a new product on DB
    public Product CreateProduct(Product product){
        // variable to check if a product is invalid to be added
        boolean invalid = false;
        try {
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            LocalDateTime date = LocalDateTime.now();
            product.setInDate(date);

            // checks if the product name exists on the custom product table
            query = "select * from product where name = '"+product.getName().trim()+"'";
            rs = st.executeQuery(query);

            if (rs.next()){
                invalid=true;
            }

            // checks if the product exists on the products with existing barcode table
            query = "select * from product_wcode where id = '"+product.getId().toString()+"'";
            rs = st.executeQuery(query);

            if (rs.next()){
                invalid=true;
            }

            // if the product already exists it will return an empty product
            if (invalid == true){
                JOptionPane.showMessageDialog(null,"El codigo de barras ya está en uso","Error",JOptionPane.ERROR_MESSAGE);
                product.setId(null);
                return product;
            }

            // checks if the product name exists on the products with existing barcode table
            query = "select * from product_wcode where name = '"+product.getName().trim()+"'";
            rs = st.executeQuery(query);

            if (rs.next()){
                invalid=true;
            }

            // if a product has the given name it will return an empty product
            if (invalid == true){
                JOptionPane.showMessageDialog(null,"El nombre ya está en uso","Error",JOptionPane.ERROR_MESSAGE);
                product.setId(null);
            }else {
                // check if there is given an existing barcode to save it on the custom products table or the products with existing barcode table
                if (product.getId()!=null){
                    query = "insert into product_wcode (id,name,price,quantity,indate) values ('"+product.getId()+"','" + product.getName().trim() + "','" + product.getPrice() + "','" + product.getQuantity() + "','" + product.getInDate() + "');";
                }else {
                    query = "insert into product (name,price,quantity,indate) values ('" + product.getName().trim() + "','" + product.getPrice() + "','" + product.getQuantity() + "','" + product.getInDate() + "');";
                }
                st.execute(query);

                // if the created product is a custom one will get the custom id to return it to be able to register movement
                if (product.getId() == null) {
                    query = "select * from product where name = '" + product.getName().trim() + "'";
                    rs = st.executeQuery(query);

                    if (rs.next()) {
                        product.setId(rs.getLong("id"));
                    }
                }

                JOptionPane.showMessageDialog(null, "El producto se guardo correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            }

            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,err,"Error",JOptionPane.ERROR_MESSAGE);
        }
        return product;
    }

    // get all products
    public ArrayList<Product> SearchProduct(String id, String name){
        // list of found products
        ArrayList<Product> products = new ArrayList<Product>();
        try{
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // makes a query searching by name or by id on the custom products table
            if (name == null) {
                query = "select * from product where id='" + id + "'";
            } else if (id == null) {
                query = "select * from product where name like '%" + name.trim() + "%' order by id asc";
            }

            rs = st.executeQuery(query);
            while (rs.next()){
                // add products to list
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setQuantity(rs.getInt("quantity"));
                products.add(product);
            }

            // makes a query searching by name or by id on the products with existing barcode table
            if (name == null) {
                query = "select * from product_wcode where id='" + id + "'";
            } else if (id == null) {
                query = "select * from product_wcode where name like '%" + name.trim() + "%' order by id asc";
            }

            rs = st.executeQuery(query);
            while (rs.next()){
                // add products to list
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setQuantity(rs.getInt("quantity"));
                products.add(product);
            }
            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,"Producto no encontrado","Error",JOptionPane.ERROR_MESSAGE);
        }

        return products;
    }

    // get all products with quantity > 5
    public ArrayList<Product> SearchGoodProducts(){
        // list of found products
        ArrayList<Product> products = new ArrayList<Product>();
        try{
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // makes a query on the custom products table
            query = "select * from product where quantity>5 order by quantity desc;";

            rs = st.executeQuery(query);
            while (rs.next()){
                // add products to list
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setQuantity(rs.getInt("quantity"));
                products.add(product);
            }

            // makes a query on the products with existing barcode table
            query = "select * from product_wcode where quantity>5 order by quantity desc;";

            rs = st.executeQuery(query);
            while (rs.next()){
                // add products to list
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setQuantity(rs.getInt("quantity"));
                products.add(product);
            }
            st.close();
            connection.close();
        } catch (Exception err) {
        }

        return products;
    }

    // get all products with 0 < quantity <= 5
    public ArrayList<Product> SearchMediumProducts(){
        // list of found products
        ArrayList<Product> products = new ArrayList<Product>();
        try{
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // makes a query on the custom products table
            query = "select * from product where quantity<=5 and quantity>0 order by quantity desc;";

            rs = st.executeQuery(query);
            while (rs.next()){
                // add products to list
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setQuantity(rs.getInt("quantity"));
                products.add(product);
            }

            // makes a query on the products with existing barcode table
            query = "select * from product_wcode where quantity<=5 and quantity>0 order by quantity desc;";

            rs = st.executeQuery(query);
            while (rs.next()){
                // add products to list
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setQuantity(rs.getInt("quantity"));
                products.add(product);
            }
            st.close();
            connection.close();
        } catch (Exception err) {
        }

        return products;
    }

    // get all products out of stock
    public ArrayList<Product> SearchBadProducts(){
        // list of found products
        ArrayList<Product> products = new ArrayList<Product>();
        try{
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // makes a query on the custom products table
            query = "select * from product where quantity=0 order by quantity desc;";

            rs = st.executeQuery(query);
            while (rs.next()){
                // add products to list
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setQuantity(rs.getInt("quantity"));
                products.add(product);
            }

            // makes a query on the products with existing barcode table
            query = "select * from product_wcode where quantity=0 order by quantity desc;";

            rs = st.executeQuery(query);
            while (rs.next()){
                // add products to list
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setQuantity(rs.getInt("quantity"));
                products.add(product);
            }
            st.close();
            connection.close();
        } catch (Exception err) {
        }

        return products;
    }

    // edits an existing product on DB
    public ArrayList<String> EditProduct(String id, Product product) {
        // list to register the changes on product to save the movement
        ArrayList<String> res = new ArrayList<String>();
        try {
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            LocalDateTime date = LocalDateTime.now();
            product.setActDate(date);

            // check if the product to edit is of the custom products table or products with existing barcode table
            if (id.length()<=3) {
                query = "select * from product where name = '" + product.getName() + "'";
                rs = st.executeQuery(query);

                // variables to save the changed values
                Integer quantityDiff = 0;
                Integer priceDiff = 0;
                Integer nameDiff = 0;
                String originalName = "";

                // check if the new name is already on use
                if (rs.next() && !(rs.getLong("id") == Long.parseLong(id))) {
                    JOptionPane.showMessageDialog(null, "El nombre ya está en uso", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    query = "select * from product where id = '" + id + "'";
                    rs = st.executeQuery(query);
                    rs.next();

                    // compares the values of the new product with the older one to save differences
                    originalName = rs.getString("name");
                    quantityDiff = rs.getInt("quantity") - product.getQuantity();
                    priceDiff = rs.getInt("price") - product.getPrice();

                    if (!rs.getString("name").equals(product.getName().trim())) {
                        nameDiff = 1;
                    }

                    // check if there are changes on the product
                    if (quantityDiff == 0 && priceDiff == 0 && rs.getString("name").equals(product.getName().trim())) {
                        JOptionPane.showMessageDialog(null, "No has cambiado nada", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // make the query to edit the product
                        query = "update product set name = '" + product.getName().trim() + "',price = '" + product.getPrice() + "',quantity = '" + product.getQuantity() + "',actdate = '" + product.getActDate() + "' where id = '" + id + "';";
                        st.execute(query);
                        JOptionPane.showMessageDialog(null, "El producto se editó correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                // add to list the changes on product
                res.add(quantityDiff.toString());
                res.add(priceDiff.toString());
                res.add(nameDiff.toString());
                res.add(originalName);
            }else{
                query = "select * from product_wcode where name = '" + product.getName() + "'";
                rs = st.executeQuery(query);

                // variables to save the changed values
                Integer quantityDiff = 0;
                Integer priceDiff = 0;
                Integer nameDiff = 0;
                String originalName = "";

                // check if the new name is already on use
                if (rs.next() && !(rs.getLong("id") == Long.parseLong(id))) {
                    JOptionPane.showMessageDialog(null, "El nombre ya está en uso", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    query = "select * from product_wcode where id = '" + id + "'";
                    rs = st.executeQuery(query);
                    rs.next();

                    // compares the values of the new product with the older one to save differences
                    originalName = rs.getString("name");
                    quantityDiff = rs.getInt("quantity") - product.getQuantity();
                    priceDiff = rs.getInt("price") - product.getPrice();

                    if (!rs.getString("name").equals(product.getName().trim())) {
                        nameDiff = 1;
                    }

                    // check if there are changes on the product
                    if (quantityDiff == 0 && priceDiff == 0 && rs.getString("name").equals(product.getName().trim())) {
                        JOptionPane.showMessageDialog(null, "No has cambiado nada", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // make the query to edit the product
                        query = "update product_wcode set name = '" + product.getName().trim() + "',price = '" + product.getPrice() + "',quantity = '" + product.getQuantity() + "',actdate = '" + product.getActDate() + "' where id = '" + id + "';";
                        st.execute(query);
                        JOptionPane.showMessageDialog(null, "El producto se editó correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                // add to list the changes on product
                res.add(quantityDiff.toString());
                res.add(priceDiff.toString());
                res.add(nameDiff.toString());
                res.add(originalName);
            }
            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,"El producto no se guardo "+err,"Error",JOptionPane.ERROR_MESSAGE);
        }
        return res;
    }

    // changes the quantity of the given products when a sell is realized
    public void SellProducts(ArrayList<Product> products) {
        try {
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            LocalDateTime date = LocalDateTime.now();

            // if the product is in the custom product table it will change the quantity
            for (int i = 0; i < products.size(); i++) {
                products.get(i).setActDate(date);
                query = "update product set quantity = '" + products.get(i).getQuantity() + "' where id = '" + products.get(i).getId() +"';";
                st.execute(query);
            }

            // if the product is in the products with existing barcode table it will change the quantity
            for (int i = 0; i < products.size(); i++) {
                products.get(i).setActDate(date);
                query = "update product_wcode set quantity = '" + products.get(i).getQuantity() + "' where id = '" + products.get(i).getId() +"';";
                st.execute(query);
            }

            st.close();
            connection.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,"El producto no se vendió "+err,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // deletes a product by its id
    public void DeleteProduct(String id) {
        try {
            ConnectionSQL connectionSQL = new ConnectionSQL();
            Connection connection = connectionSQL.connect();

            st = connection.createStatement();

            // make the query to delete it
            query = "delete from product where id = '" + id +"';";
            st.execute(query);

            st.close();
            connection.close();
            JOptionPane.showMessageDialog(null, "El producto se borró correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null,"El producto no se borró "+err,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
