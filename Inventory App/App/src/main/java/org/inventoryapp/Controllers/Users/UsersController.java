package org.inventoryapp.Controllers.Users;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.inventoryapp.Classes.User.User;
import org.inventoryapp.Controllers.User.UserController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

public class UsersController {
    // make global list for users
    public ArrayList<User> usersList;

    // function to fill the table
    public DefaultTableModel fillTable(ArrayList<User> user) {
        // make the table non-editable
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // create columns
        model.addColumn("Nombres");
        model.addColumn("Usuario");
        model.addColumn("Contraseña");
        model.addColumn("Rol");

        // read all users
        for (int i = 0; i < user.size(); i++) {
            // changes the user role to be shown on GUI
            if (user.get(i).getRole().equals("Employee")){
                user.get(i).setRole("Empleado");
            } else {
                user.get(i).setRole("Administrador");
            }
            // create a table row and adds it to the table
            String[] data = new String[]{user.get(i).getName().toString(),user.get(i).getUsername().toString(), user.get(i).getPassword(), user.get(i).getRole()};
            model.addRow(data);
        }
        return model;
    }

    // searches all users from DB
    public void searchAllUsers(JTable users) {
        UserController userController = new UserController();
        try {
            // make the query and save the users on the global list
            usersList = userController.SearchAllUsers();
            // deletes admin user to don't show the user information
            for (int i = 0; i < usersList.size(); i++) {
                if (usersList.get(i).getUsername().equals("admin")){
                    usersList.remove(i);
                }
            }
        } catch (Exception err) {
        }
        // fill the table with found users
        if (usersList.size()!=0) {
            users.setModel(fillTable(usersList));
        }
    }

    // reads the table and creates a pdf document
    public void downloadPdf(JTable users) throws IOException, DocumentException {
        Document doc = new Document();

        // creates a directory to save files if it doesn't exist
        if (Files.notExists(Path.of(System.getProperty("user.dir") + "/Reportes/Pdf"))){
            Files.createDirectory(Path.of(System.getProperty("user.dir") + "/Reportes/Pdf"));
        }

        // creates the pdf file
        String path = System.getProperty("user.dir")+"/Reportes/Pdf/Usuarios "+ LocalDate.now().toString()+".pdf";
        PdfWriter.getInstance(doc, new FileOutputStream(path));
        doc.open();

        PdfPTable pdfTable;
        PdfPCell cell;

        // create the document table according to the content of the tables
        if (users.getColumnCount()>0) {
            pdfTable = new PdfPTable(users.getColumnCount());

            pdfTable.setHeaderRows(1);

            // create the column headers
            for (int i = 0; i < users.getColumnCount(); i++) {
                cell = new PdfPCell(new Paragraph(users.getColumnName(i)));
                cell.setBackgroundColor(new GrayColor(0.7f));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfTable.addCell(cell);
            }

            // fill the table rows
            for (int i = 0; i < users.getRowCount(); i++) {
                for (int j = 0; j < users.getColumnCount(); j++) {
                    cell = new PdfPCell(Phrase.getInstance(users.getModel().getValueAt(i, j).toString()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    pdfTable.addCell(cell);
                }
            }

            doc.add(pdfTable);
        }

        doc.close();
        JOptionPane.showMessageDialog(null, "El reporte de usuarios se descargó correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }

    // reads the tables and creates an Excel document
    public void downloadExcel(JTable users) throws IOException {
        new WorkbookFactory();
        Workbook wb = new XSSFWorkbook();
        // create sheet to save each table
        Sheet sheet = wb.createSheet();
        Row row = sheet.createRow(2);
        TableModel model = users.getModel();

        // setting column headers
        Row headerRow = sheet.createRow(0);
        for (int headings = 0; headings<model.getColumnCount(); headings++){
            headerRow.createCell(headings).setCellValue(model.getColumnName(headings));
        }
        // fill the Excel with table data
        for (int rows = 0; rows<model.getRowCount(); rows++){
            for(int cols = 0; cols < users.getColumnCount(); cols++){
                row.createCell(cols).setCellValue(model.getValueAt(rows, cols).toString());
                CellStyle style = wb.createCellStyle();
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                row.getCell(cols).setCellStyle(style);
                row.setHeight((short)-1);
            }
            row = sheet.createRow(rows+3);
        }

        // make the column size according to content
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);

        // creates a directory to save files if it doesn't exist
        if (Files.notExists(Path.of(System.getProperty("user.dir") + "/Reportes/Excel"))){
            Files.createDirectory(Path.of(System.getProperty("user.dir") + "/Reportes/Excel"));
        }

        // creates the Excel file
        String path = System.getProperty("user.dir")+"/Reportes/Excel/Usuarios "+ LocalDate.now().toString()+".xlsx";
        wb.write(new FileOutputStream(path));
        JOptionPane.showMessageDialog(null, "El reporte de usuarios se descargó correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }

    // show the users from the global list in the GUI
    public void showUsers(JTable users) throws IOException, FontFormatException {
        usersList = new ArrayList<User>();

        searchAllUsers(users);

        // make a cell renderer
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                renderer.setHorizontalAlignment(CENTER);

                return renderer;
            }
        };

        // fills the GUI table with the content of the global variables
        if (usersList.size()!=0){
            for (int i = 0; i <= 3; i++) {
                users.getColumnModel().getColumn(i).setCellRenderer(r);
            }
            users.setRowHeight(30);
        }
    }
}
