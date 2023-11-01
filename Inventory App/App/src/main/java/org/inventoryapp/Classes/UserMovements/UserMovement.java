package org.inventoryapp.Classes.UserMovements;

import java.time.LocalDateTime;

public class UserMovement {
    private Integer id;
    private String username;
    private String type;
    private LocalDateTime date;
    private String user;

    public UserMovement() {}

    public UserMovement(Integer id, String username, String type, LocalDateTime date, String user) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.date = date;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserMovement{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", date=" + date +
                ", user='" + user + '\'' +
                '}';
    }
}
