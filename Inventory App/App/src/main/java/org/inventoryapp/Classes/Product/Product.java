package org.inventoryapp.Classes.Product;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import java.time.LocalDateTime;

public class Product {

    private Long id;
    private String name;
    private Integer price;
    private Integer quantity;
    private LocalDateTime inDate;
    private LocalDateTime actDate;

    public Product() {}

    public Product(Long id, String name, Integer price, Integer quantity, LocalDateTime inDate, LocalDateTime actDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.inDate = inDate;
        this.actDate = actDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getInDate() {
        return inDate;
    }

    public void setInDate(LocalDateTime inDate) {
        this.inDate = inDate;
    }

    public LocalDateTime getActDate() {
        return actDate;
    }

    public void setActDate(LocalDateTime actDate) {
        this.actDate = actDate;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", inDate=" + inDate +
                ", actDate=" + actDate +
                '}';
    }
}
