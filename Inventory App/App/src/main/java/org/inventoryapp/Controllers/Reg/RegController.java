package org.inventoryapp.Controllers.Reg;

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
import org.inventoryapp.Classes.Movements.Movement;
import org.inventoryapp.Controllers.Movements.MovementController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RegController {
    // global list of movements
    ArrayList<Movement> movements;

    // function to fill the table with the movements
    public DefaultTableModel fillTable(ArrayList<Movement> movements) {
        // make the table non-editable
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // create columns
        model.addColumn("Codigo Producto");
        model.addColumn("Nombre Producto");
        model.addColumn("Tipo de Movimiento");
        model.addColumn("Fecha");
        model.addColumn("Usuario");

        String prefix;

        // read all movements
        for (int i = 0; i < movements.size(); i++) {
            if (movements.get(i).getProductId().toString().length()==1){
                prefix = "00";
            } else if (movements.get(i).getProductId().toString().length()==2){
                prefix = "0";
            } else {
                prefix = "";
            }

            // according to the type of movement it will change it to be shown on the GUI
            if (movements.get(i).getType().equals("create")){
                movements.get(i).setType("Creacion producto");
            }
            if (movements.get(i).getType().startsWith("sell: ")){
                movements.get(i).setType("Se vendieron "+movements.get(i).getType().replace("sell: ","")+" unidades del producto");
            }
            if (movements.get(i).getType().startsWith("sell - out of stock: ")){
                movements.get(i).setType("Se vendieron "+movements.get(i).getType().replace("sell - out of stock: ","")+" unidades del producto y se acabaron las existencias del producto");
            }
            if (movements.get(i).getType().startsWith("name change:")){
                String type = movements.get(i).getType().replace("name change: ","");
                String[] names = type.split("-");
                movements.get(i).setType("Cambio de nombre: "+names[0]+" -> "+names[1]);
            }
            if (movements.get(i).getType().startsWith("in: ")){
                movements.get(i).setType("Aumento de existencias: "+movements.get(i).getType().replace("in: ","").replace("-",""));
            }
            if (movements.get(i).getType().startsWith("out:")){
                movements.get(i).setType("Disminucion de existencias: "+movements.get(i).getType().replace("out: ","").replace("-",""));
            }
            if (movements.get(i).getType().startsWith("up:")){
                movements.get(i).setType("Aumento en el precio: "+movements.get(i).getType().replace("up: ","").replace("-",""));
            }
            if (movements.get(i).getType().startsWith("down:")){
                movements.get(i).setType("Disminucion en el precio: "+movements.get(i).getType().replace("down: ","").replace("-",""));
            }
            if (movements.get(i).getType().equals("delete")){
                movements.get(i).setType("Eliminacion del producto");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY - HH:mm:ss");

            // create a table row and adds it to the table
            String[] data = new String[]{prefix + movements.get(i).getProductId().toString(), movements.get(i).getProductName(), movements.get(i).getType(),formatter.format(movements.get(i).getDate()),movements.get(i).getUsername()};
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
        String path = System.getProperty("user.dir")+"/Reportes/Pdf/Reporte "+ LocalDate.now().toString()+".pdf";
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
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);

        // creates a directory to save files if it doesn't exist
        if (Files.notExists(Path.of(System.getProperty("user.dir") + "/Reportes/Excel"))){
            Files.createDirectory(Path.of(System.getProperty("user.dir") + "/Reportes/Excel"));
        }

        // creates the Excel file
        String path = System.getProperty("user.dir")+"/Reportes/Excel/Reporte "+ LocalDate.now().toString()+".xlsx";
        wb.write(new FileOutputStream(path));
        JOptionPane.showMessageDialog(null, "El reporte se descargó correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }

    // get the movements of a specific product
    public void getProductMovements(LocalDate date,JTable regTable,JTextField id,Integer fil,JTextField username) {
        movements = new ArrayList<Movement>();
        try {
            MovementController movementController = new MovementController();

            String Id = id.getText();

            try {
                Long.parseLong(Id);

                // if the id field is numeric searches the movement by product id
                movements = movementController.getProductMovements(date,Id, null,fil,username);
            } catch (Exception err) {
                // if the id field isn't numeric searches the movement by product name
                movements = movementController.getProductMovements(date,null, Id.toLowerCase(),fil,username);
            }
        } catch (Exception err){
        }
        // check if query returned at least one movement
        if (!movements.isEmpty()) {
            // lists for filter the products and show only non-repeated products
            ArrayList<Long> indexes = new ArrayList<Long>();
            ArrayList<String> names = new ArrayList<String>();
            for (int i = 0; i < movements.size(); i++) {
                if (!indexes.contains(movements.get(i).getProductId())){
                    indexes.add(movements.get(i).getProductId());
                }
                if (!names.contains(movements.get(i).getProductName())){
                    names.add(movements.get(i).getProductName());
                }
            }
            // if non-repeated products list size is one, fills the table with the movements of that product
            if (indexes.size()==1) {
                regTable.setModel(fillTable(movements));
            // if non-repeated products list size is greater than one
            }else{
                // makes an array for the table rows to select the product
                Object[][] rows = new Object[indexes.size()][2];
                for (int i = 0; i < indexes.size(); i++) {
                    Object[] row = {indexes.get(i),names.get(i)};
                    rows[i] = row;
                }
                Object[] cols = {"Codigo","Nombre"};
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
                ArrayList<Movement> movementsFil = new ArrayList<Movement>();
                try {
                    // add to global list the selected product
                    for (int i = 0; i < movements.size(); i++) {
                        if (movements.get(i).getProductId() == indexes.get(Integer.parseInt(index.getText()))) {
                            movementsFil.add(movements.get(i));
                        }
                    }
                    // fills the table with the movements of that product
                    regTable.setModel(fillTable(movementsFil));
                } catch (Exception err) {}
            }
        } else {
            JOptionPane.showMessageDialog(null,"No se encuentra el producto o no hay registro del producto en la fecha especificada","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // get all movements from DB
    public void getAllMovements(LocalDate date,JTable regTable,Integer fil, JTextField username) {
        movements = new ArrayList<Movement>();
        // search movements
        try {
            MovementController movementController = new MovementController();
            movements = movementController.getAllMovements(date,fil,username);
        } catch (Exception err){
        }
        // check if the return of query is empty
        if (!movements.isEmpty()) {
            regTable.setModel(fillTable(movements));
        } else {
            JOptionPane.showMessageDialog(null,"No se encuentra el producto o no hay registro del producto en la fecha especificada","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // controllers for the id field
    public void MainController(JTextField id, JDateChooser dateChooser,JTable regTable, JComboBox filter, JTextField username){
        if (id.getText().isEmpty()){
            // get date from datechooser and check if it's valid
            LocalDate date = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (date.getMonthValue()<LocalDate.now().getMonthValue() && date.getDayOfMonth()<LocalDate.now().getDayOfMonth() || date.getYear()<LocalDate.now().getYear()){
                JOptionPane.showMessageDialog(null,"La fecha no puede ser mayor a 1 mes","Error",JOptionPane.ERROR_MESSAGE);
            } else if (date.getMonthValue()>=LocalDate.now().getMonthValue() && date.getDayOfMonth()>LocalDate.now().getDayOfMonth() || date.getYear()>LocalDate.now().getYear()){
                JOptionPane.showMessageDialog(null,"Fecha incorrecta","Error",JOptionPane.ERROR_MESSAGE);
            } else {
                Integer fil = filter.getSelectedIndex();
                getAllMovements(date,regTable,fil,username);
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
                getProductMovements(date,regTable,id,fil,username);
            }
        }
    }
}
