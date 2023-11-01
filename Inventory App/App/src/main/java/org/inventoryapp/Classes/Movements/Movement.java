package org.inventoryapp.Classes.Movements;

import java.time.LocalDateTime;

public class Movement {
    private Integer id;
    private Long productId;
    private String productName;
    private String type;
    private LocalDateTime date;
    private String username;

    public Movement() {}

    public Movement(Integer id, Long productId, String productName, String type, LocalDateTime date, String username) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.date = date;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Movement{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", type='" + type + '\'' +
                ", date=" + date +
                ", username='" + username + '\'' +
                '}';
    }
}
