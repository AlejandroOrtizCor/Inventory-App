package org.inventoryapp.Controllers.RegUser;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.toedter.calendar.JDateChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.inventoryapp.Classes.UserMovements.UserMovement;
import org.inventoryapp.Controllers.UserMovement.UserMovementController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RegUserController {
    // global list of movements
    ArrayList<UserMovement> movements;

    // function to fill the table with the movements
    public DefaultTableModel fillTable(ArrayList<UserMovement> movements) {
        // make the table non-editable
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // create columns
        model.addColumn("Usuario");
        model.addColumn("Tipo de Movimiento");
        model.addColumn("Fecha");
        model.addColumn("Usuario editor");

        // read all movements
        for (int i = 0; i < movements.size(); i++) {
            // according to the type of movement it will change it to be shown on the GUI
            if (movements.get(i).getType().equals("login")){
                movements.get(i).setType("Conexión");
            }
            if (movements.get(i).getType().equals("logout")){
                movements.get(i).setType("Desconexión");
            }
            if (movements.get(i).getType().equals("create")){
                movements.get(i).setType("Creación de usuario");
            }
            if (movements.get(i).getType().equals("delete")){
                movements.get(i).setType("Eliminación de usuario");
            }
            if (movements.get(i).getType().startsWith("edit name:")){
                String type = movements.get(i).getType().replace("edit name:","");
                String[] names = type.split("-");
                String[] user1 = names[0].split("/");
                String[] user2 = names[1].split("/");
                movements.get(i).setType("Edición de nombre: "+user1[0]+" ("+user1[1]+") -> "+user2[0]+" ("+user2[1]+")");
            }
            if (movements.get(i).getType().startsWith("edit password:")){
                String type = movements.get(i).getType().replace("edit password:","");
                String[] pass = type.split("¿");
                movements.get(i).setType("Edición de contraseña: "+pass[0]+" -> "+pass[1]);
            }
            if (movements.get(i).getType().startsWith("edit role:")){
                String type = movements.get(i).getType().replace("edit role:","");
                String[] role = type.split("-");
                if (role[0].equals("Admin")){
                    role[0] = "Administrador";
                }else{
                    role[0] = "Empleado";
                }
                if (role[1].equals("Admin")){
                    role[1] = "Administrador";
                }else{
                    role[1] = "Empleado";
                }
                movements.get(i).setType("Edición de rol: "+role[0]+" -> "+role[1]);
            }

            if (movements.get(i).getUser().equals("null")){
                movements.get(i).setUser("No Aplica");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY - HH:mm:ss");

            // create a table row and adds it to the table
            String[] data = new String[]{movements.get(i).getUsername(), movements.get(i).getType(),formatter.format(movements.get(i).getDate()),movements.get(i).getUser()};
            model.addRow(data);
        }
        return model;
    }

    // reads the table and creates a pdf document
    public void downloadPdf(JTable regTable) throws IOException, DocumentException {
        Document doc = new Document();

        // creates a directory to save files if it doesn't exist
        if (Files.notExists(Path.of(System.getProperty("user.dir") + "/Reportes/Pdf"))){
            Files.createDirectory(Path.of(System.getProperty("user.dir") + "/Reportes/Pdf"));
        }

        // creates the pdf file
        String path = System.getProperty("user.dir")+"/Reportes/Pdf/Reporte Usuarios "+ LocalDate.now().toString()+".pdf";
        PdfWriter.getInstance(doc, new FileOutputStream(path));
        doc.open();

        PdfPTable pdfTable = new PdfPTable(regTable.getColumnCount());

        pdfTable.setHeaderRows(1);

        PdfPCell cell;

        // create the column headers
        for (int i = 0; i < regTable.getColumnCount(); i++) {
            cell = new PdfPCell(new Paragraph(regTable.getColumnName(i)));
            cell.setBackgroundColor(new GrayColor(0.7f));
            pdfTable.addCell(cell);
        }

        // fill the table rows
        for (int i = 0; i < regTable.getRowCount(); i++) {
            for (int j = 0; j < regTable.getColumnCount(); j++) {
                pdfTable.addCell(regTable.getModel().getValueAt(i,j).toString());
            }
        }

        doc.add(pdfTable);
        doc.close();
        JOptionPane.showMessageDialog(null, "El reporte se descargó correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }

    // reads the table and creates an Excel document
    public void downloadExcel(JTable regTable) throws IOException {
        new WorkbookFactory();
        Workbook wb = new XSSFWorkbook();
        // create sheet to save the table
        Sheet sheet = wb.createSheet();
        Row row = sheet.createRow(2);
        TableModel model = regTable.getModel();

        // setting column headers
        Row headerRow = sheet.createRow(0);
        for (int headings = 0; headings<model.getColumnCount(); headings++){
            headerRow.createCell(headings).setCellValue(model.getColumnName(headings));
        }
        // fill the Excel with table data
        for (int rows = 0; rows<model.getRowCount(); rows++){
            for(int cols = 0; cols < regTable.getColumnCount(); cols++){
                row.createCell(cols).setCellValue(model.getValueAt(rows, cols).toString());
                CellStyle style = wb.createCellStyle();
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                row.getCell(cols).setCellStyle(style);
            }
            row = sheet.createRow(rows+3);
        }

        // make the column size according to content
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        // creates a directory to save files if it doesn't exist
        if (Files.notExists(Path.of(System.getProperty("user.dir") + "/Reportes/Excel"))){
            Files.createDirectory(Path.of(System.getProperty("user.dir") + "/Reportes/Excel"));
        }

        // creates the Excel file
        String path = System.getProperty("user.dir")+"/Reportes/Excel/Reporte Usuarios "+ LocalDate.now().toString()+".xlsx";
        wb.write(new FileOutputStream(path));
        JOptionPane.showMessageDialog(null, "El reporte se descargó correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }

    // get the movements of a specific user
    public void getUserMovements(LocalDate date,JTable regTable,JTextField username,Integer fil) {
        movements = new ArrayList<UserMovement>();
        // search movements
        try {
            UserMovementController userMovementController = new UserMovementController();
            movements = userMovementController.getUserMovements(date,username.getText(),fil);
        } catch (Exception err){
        }
        // check if the return of query is empty
        if (!movements.isEmpty()) {
            regTable.setModel(fillTable(movements));
        } else {
            JOptionPane.showMessageDialog(null,"No se encuentra el producto o no hay registro del producto en la fecha especificada","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // get all movements from DB
    public void getAllMovements(LocalDate date,JTable regTable,Integer fil) {
        movements = new ArrayList<UserMovement>();
        // search movements
        try {
            UserMovementController userMovementController = new UserMovementController();
            movements = userMovementController.getAllMovements(date,fil);
        } catch (Exception err){
        }
        // check if the return of query is empty
        if (!movements.isEmpty()) {
            regTable.setModel(fillTable(movements));
        } else {
            JOptionPane.showMessageDialog(null,"No se encuentra el usuario o no hay registro del usuario en la fecha especificada","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // controllers for the username field
    public void MainController(JTextField username, JDateChooser dateChooser, JTable regTable, JComboBox filter){
        if (username.getText().isEmpty()){
            // get date from datechooser and check if it's valid
            LocalDate date = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (date.getMonthValue()<LocalDate.now().getMonthValue() && date.getDayOfMonth()<LocalDate.now().getDayOfMonth() || date.getYear()<LocalDate.now().getYear()){
                JOptionPane.showMessageDialog(null,"La fecha no puede ser mayor a 1 mes","Error",JOptionPane.ERROR_MESSAGE);
            } else if (date.getMonthValue()>=LocalDate.now().getMonthValue() && date.getDayOfMonth()>LocalDate.now().getDayOfMonth() || date.getYear()>LocalDate.now().getYear()){
                JOptionPane.showMessageDialog(null,"Fecha incorrecta","Error",JOptionPane.ERROR_MESSAGE);
            } else {
                Integer fil = filter.getSelectedIndex();
                getAllMovements(date,regTable,fil);
            }
        } else {
            // get date from datechooser and check if it's valid
            LocalDate date = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (date.getMonthValue()<LocalDate.now().getMonthValue() && date.getDayOfMonth()<LocalDate.now().getDayOfMonth() || date.getYear()<LocalDate.now().getYear()){
                JOptionPane.showMessageDialog(null,"La fecha no puede ser mayor a 1 mes","Error",JOptionPane.ERROR_MESSAGE);
            } else if (date.getMonthValue()>=LocalDate.now().getMonthValue() && date.getDayOfMonth()>LocalDate.now().getDayOfMonth() || date.getYear()>LocalDate.now().getYear()){
                JOptionPane.showMessageDialog(null,"Fecha incorrecta","Error",JOptionPane.ERROR_MESSAGE);
            } else {
                Integer fil = filter.getSelectedIndex();
                getUserMovements(date,regTable,username,fil);
            }
        }
    }
}
