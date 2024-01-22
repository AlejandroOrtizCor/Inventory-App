package org.inventoryapp;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import org.inventoryapp.GUI.LoginGUI;

public class Main {
    public static void main(String[] args){
        // open interface
        LoginGUI login = new LoginGUI();
        login.setVisible(true);
    }
}