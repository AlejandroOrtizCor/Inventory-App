package org.inventoryapp.Controllers.Inventory;

/**
 * @author Alejandro Ortiz
 * @project Inventory-App
 */

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.inventoryapp.Classes.Product.Product;
import org.inventoryapp.Controllers.Product.ProductController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

public class InventoryController {
    // make global lists for products according to stock
    public ArrayList<Product> productsListGood;
    public ArrayList<Product> productsListMedium;
    public ArrayList<Product> productsListBad;

    // functions to make the ean 13 barcode
    private char A(char num){
        char code = 0;
        if (num=='0'){
            code = 'A';
        }else if (num=='1'){
            code = 'B';
        }else if (num=='2'){
            code = 'C';
        }else if (num=='3'){
            code = 'D';
        }else if (num=='4'){
            code = 'E';
        }else if (num=='5'){
            code = 'F';
        }else if (num=='6'){
            code = 'G';
        }else if (num=='7'){
            code = 'H';
        }else if (num=='8'){
            code = 'I';
        }else if (num=='9'){
            code = 'J';
        }
        return code;
    }

    private char B(char num){
        char code = 0;
        if (num=='0'){
            code = 'K';
        }else if (num=='1'){
            code = 'L';
        }else if (num=='2'){
            code = 'M';
        }else if (num=='3'){
            code = 'N';
        }else if (num=='4'){
            code = 'O';
        }else if (num=='5'){
            code = 'P';
        }else if (num=='6'){
            code = 'Q';
        }else if (num=='7'){
            code = 'R';
        }else if (num=='8'){
            code = 'S';
        }else if (num=='9'){
            code = 'T';
        }
        return code;
    }

    private char C(char num){
        char code = 0;
        if (num=='0'){
            code = 'a';
        }else if (num=='1'){
            code = 'b';
        }else if (num=='2'){
            code = 'c';
        }else if (num=='3'){
            code = 'd';
        }else if (num=='4'){
            code = 'e';
        }else if (num=='5'){
            code = 'f';
        }else if (num=='6'){
            code = 'g';
        }else if (num=='7'){
            code = 'h';
        }else if (num=='8'){
            code = 'i';
        }else if (num=='9'){
            code = 'j';
        }
        return code;
    }

    // function to fill the tables
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
        model.addColumn("Existencias");
        model.addColumn("Codigo de barras");

        // prefix for custom products
        String prefix;

        // read all products
        for (int i = 0; i < product.size(); i++) {
            // put prefix if it's a custom product
            if (product.get(i).getId().toString().length()==1){
                prefix = "00";
            } else if (product.get(i).getId().toString().length()==2){
                prefix = "0";
            } else {
                prefix = "";
            }

            // row table
            String[] data;

            // if the product is custom
            if ((prefix+product.get(i).getId().toString()).length()<=3) {
                data = new String[]{prefix + product.get(i).getId().toString(), product.get(i).getName(), product.get(i).getPrice().toString(), product.get(i).getQuantity().toString(), "*" + prefix + product.get(i).getId().toString() + "*"};
            // if the product barcode is ean 13
            }else if ((prefix+product.get(i).getId().toString()).length()==13){
                // creates the ean 13 barcode
                String code = product.get(i).getId().toString();
                String codefinal = String.valueOf(code.charAt(0));
                String type = null;
                char n1 = code.charAt(0);
                if (n1=='0'){
                    type = "AAAAAA";
                }else if (n1=='1'){
                    type = "AABABB";
                }else if (n1=='2'){
                    type = "AABBAB";
                }else if (n1=='3'){
                    type = "AABBBA";
                }else if (n1=='4'){
                    type = "ABAABB";
                }else if (n1=='5'){
                    type = "ABBAAB";
                }else if (n1=='6'){
                    type = "ABBBAB";
                }else if (n1=='7'){
                    type = "ABABAB";
                }else if (n1=='8'){
                    type = "ABABBA";
                }else if (n1=='9'){
                    type = "ABBABA";
                }
                for (int j = 1; j < 7; j++) {
                    if (type.charAt(j-1)=='A'){
                        codefinal+=A(code.charAt(j));
                    }
                    if (type.charAt(j-1)=='B'){
                        codefinal+=B(code.charAt(j));
                    }
                }
                codefinal+="*";
                for (int j = 7; j < 13; j++) {
                    codefinal+=C(code.charAt(j));
                }
                codefinal+="+";
                data = new String[]{prefix + product.get(i).getId().toString(), product.get(i).getName(), product.get(i).getPrice().toString(), product.get(i).getQuantity().toString(), codefinal};
            // if barcode isn't ean 13 neither custom
            }else {
                data = new String[]{prefix + product.get(i).getId().toString(), product.get(i).getName(), product.get(i).getPrice().toString(), product.get(i).getQuantity().toString(), "No disponible"};
            }
            // add row to the table
            model.addRow(data);
        }
        return model;
    }

    // searches all the products from DB
    public void searchAllProducts(JTable GoodInventory, JTable MediumInventory, JTable BadInventory) {
        ProductController productController = new ProductController();
        // fills the global variables with the result of the queries in DB
        try {
            productsListGood = productController.SearchGoodProducts();
            productsListMedium = productController.SearchMediumProducts();
            productsListBad = productController.SearchBadProducts();
        } catch (Exception err) {
        }
        // fills the tables with the global variables
        if (productsListGood.size()!=0) {
            GoodInventory.setModel(fillTable(productsListGood));
        }
        if (productsListMedium.size()!=0) {
            MediumInventory.setModel(fillTable(productsListMedium));
        }
        if (productsListBad.size()!=0) {
            BadInventory.setModel(fillTable(productsListBad));
        }
    }

    // reads the tables and creates a pdf document
    public void downloadPdf(JTable GoodInventory, JTable MediumInventory, JTable BadInventory) throws IOException, DocumentException {
        Document doc = new Document();

        // creates a directory to save files if it doesn't exist
        if (Files.notExists(Path.of(System.getProperty("user.dir") + "/Reportes/Pdf"))){
            Files.createDirectory(Path.of(System.getProperty("user.dir") + "/Reportes/Pdf"));
        }

        // creates the pdf file
        String path = System.getProperty("user.dir")+"/Reportes/Pdf/Inventario "+ LocalDate.now().toString()+".pdf";
        PdfWriter.getInstance(doc, new FileOutputStream(path));
        doc.open();

        // creates variables for the document
        PdfPTable pdfTable;
        PdfPCell cell;
        BaseFont bf;
        Paragraph title;

        // create the document tables according to the content of the tables
        if (GoodInventory.getColumnCount()>0) {
            // create the table title
            title = new Paragraph("Productos con existencias mayores o iguales a 5\n\n");
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            pdfTable = new PdfPTable(GoodInventory.getColumnCount());

            pdfTable.setHeaderRows(1);

            // create the column headers
            for (int i = 0; i < GoodInventory.getColumnCount(); i++) {
                cell = new PdfPCell(new Paragraph(GoodInventory.getColumnName(i)));
                cell.setBackgroundColor(new GrayColor(0.7f));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfTable.addCell(cell);
            }

            // fill the table rows
            for (int i = 0; i < GoodInventory.getRowCount(); i++) {
                for (int j = 0; j < GoodInventory.getColumnCount(); j++) {
                    if (j == 4) {
                        com.itextpdf.text.Font font;
                        if (GoodInventory.getValueAt(i, j).toString().length() <= 5) {
                            bf = BaseFont.createFont("src/res/Codebar Font/font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                            font = new com.itextpdf.text.Font(bf, 32);
                            cell = new PdfPCell(new Phrase(GoodInventory.getModel().getValueAt(i, j).toString(), font));
                        } else if (GoodInventory.getValueAt(i, j).toString().length() == 15) {
                            bf = BaseFont.createFont("src/res/Codebar Font/font2.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                            font = new com.itextpdf.text.Font(bf, 40);
                            cell = new PdfPCell(new Phrase(GoodInventory.getModel().getValueAt(i, j).toString(), font));
                        } else {
                            cell = new PdfPCell(new Phrase(GoodInventory.getModel().getValueAt(i, j).toString()));
                        }
                        cell.setFixedHeight(55f);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        pdfTable.addCell(cell);
                    } else {
                        cell = new PdfPCell(Phrase.getInstance(GoodInventory.getModel().getValueAt(i, j).toString()));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        pdfTable.addCell(cell);
                    }
                }
            }

            doc.add(pdfTable);
        }

        if (MediumInventory.getColumnCount()>0) {
            // create the table title
            title = new Paragraph("\nProductos con existencias menores a 5\n\n");
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            pdfTable = new PdfPTable(MediumInventory.getColumnCount());

            pdfTable.setHeaderRows(1);

            // create the column headers
            for (int i = 0; i < MediumInventory.getColumnCount(); i++) {
                cell = new PdfPCell(new Paragraph(MediumInventory.getColumnName(i)));
                cell.setBackgroundColor(new GrayColor(0.7f));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfTable.addCell(cell);
            }

            // fill the table rows
            for (int i = 0; i < MediumInventory.getRowCount(); i++) {
                for (int j = 0; j < MediumInventory.getColumnCount(); j++) {
                    if (j == 4) {
                        com.itextpdf.text.Font font;
                        if (MediumInventory.getValueAt(i, j).toString().length() <= 5) {
                            bf = BaseFont.createFont("src/res/Codebar Font/font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                            font = new com.itextpdf.text.Font(bf, 32);
                            cell = new PdfPCell(new Phrase(MediumInventory.getModel().getValueAt(i, j).toString(), font));
                        } else if (MediumInventory.getValueAt(i, j).toString().length() == 15) {
                            bf = BaseFont.createFont("src/res/Codebar Font/font2.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                            font = new com.itextpdf.text.Font(bf, 40);
                            cell = new PdfPCell(new Phrase(MediumInventory.getModel().getValueAt(i, j).toString(), font));
                        } else {
                            cell = new PdfPCell(new Phrase(MediumInventory.getModel().getValueAt(i, j).toString()));
                        }
                        cell.setFixedHeight(55f);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        pdfTable.addCell(cell);
                    } else {
                        cell = new PdfPCell(Phrase.getInstance(MediumInventory.getModel().getValueAt(i, j).toString()));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        pdfTable.addCell(cell);
                    }
                }
            }

            doc.add(pdfTable);
        }

        if (BadInventory.getColumnCount()>0) {
            // create the table title
            title = new Paragraph("\nSin existencias\n\n");
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            pdfTable = new PdfPTable(BadInventory.getColumnCount());

            pdfTable.setHeaderRows(1);

            // create the column headers
            for (int i = 0; i < BadInventory.getColumnCount(); i++) {
                cell = new PdfPCell(new Paragraph(BadInventory.getColumnName(i)));
                cell.setBackgroundColor(new GrayColor(0.7f));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfTable.addCell(cell);
            }

            // fill the table rows
            for (int i = 0; i < BadInventory.getRowCount(); i++) {
                for (int j = 0; j < BadInventory.getColumnCount(); j++) {
                    if (j == 4) {
                        com.itextpdf.text.Font font;
                        if (BadInventory.getValueAt(i, j).toString().length() <= 5) {
                            bf = BaseFont.createFont("src/res/Codebar Font/font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                            font = new com.itextpdf.text.Font(bf, 32);
                            cell = new PdfPCell(new Phrase(BadInventory.getModel().getValueAt(i, j).toString(), font));
                        } else if (BadInventory.getValueAt(i, j).toString().length() == 15) {
                            bf = BaseFont.createFont("src/res/Codebar Font/font2.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                            font = new com.itextpdf.text.Font(bf, 40);
                            cell = new PdfPCell(new Phrase(BadInventory.getModel().getValueAt(i, j).toString(), font));
                        } else {
                            cell = new PdfPCell(new Phrase(BadInventory.getModel().getValueAt(i, j).toString()));
                        }
                        cell.setFixedHeight(55f);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        pdfTable.addCell(cell);
                    } else {
                        cell = new PdfPCell(Phrase.getInstance(BadInventory.getModel().getValueAt(i, j).toString()));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        pdfTable.addCell(cell);
                    }
                }
            }

            doc.add(pdfTable);
        }
        doc.close();
        JOptionPane.showMessageDialog(null, "El inventario se descargó correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }

    // reads the tables and creates an Excel document
    public void downloadExcel(JTable GoodInventory, JTable MediumInventory, JTable BadInventory) throws IOException {
        new WorkbookFactory();
        Workbook wb = new XSSFWorkbook();
        // create sheet to save each table
        Sheet sheet = wb.createSheet("Productos con existencias mayores o iguales a 5");
        Row row = sheet.createRow(2);
        TableModel model = GoodInventory.getModel();

        // setting column headers
        Row headerRow = sheet.createRow(0);
        for (int headings = 0; headings<model.getColumnCount(); headings++){
            headerRow.createCell(headings).setCellValue(model.getColumnName(headings));
        }
        // fill the Excel with table data
        for (int rows = 0; rows<model.getRowCount(); rows++){
            for(int cols = 0; cols < GoodInventory.getColumnCount(); cols++){
                row.createCell(cols).setCellValue(model.getValueAt(rows, cols).toString());
                if (cols == 4){
                    org.apache.poi.ss.usermodel.Font font = wb.createFont();
                    if (model.getValueAt(rows, cols).toString().length()<=5) {
                        font.setFontName("C39HrP36DlTt");
                        short size = 720;
                        font.setFontHeight(size);
                    }else if (model.getValueAt(rows, cols).toString().length()==15) {
                        font.setFontName("Libre Barcode EAN13 Text");
                        short size = 900;
                        font.setFontHeight(size);
                    }
                    CellStyle style = wb.createCellStyle();
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    row.getCell(cols).setCellStyle(style);
                }else{
                    CellStyle style = wb.createCellStyle();
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    row.getCell(cols).setCellStyle(style);
                }
                row.setHeight((short)-1);
            }
            row = sheet.createRow(rows+3);
        }

        Sheet sheet2 = wb.createSheet("Productos con existencias menores a 5");
        row = sheet2.createRow(2);
        model = MediumInventory.getModel();

        headerRow = sheet2.createRow(0);
        for (int headings = 0; headings<model.getColumnCount(); headings++){
            headerRow.createCell(headings).setCellValue(model.getColumnName(headings));
        }
        for (int rows = 0; rows<model.getRowCount(); rows++){
            for(int cols = 0; cols < MediumInventory.getColumnCount(); cols++){
                row.createCell(cols).setCellValue(model.getValueAt(rows, cols).toString());
                if (cols == 4){
                    org.apache.poi.ss.usermodel.Font font = wb.createFont();
                    if (model.getValueAt(rows, cols).toString().length()<=5) {
                        font.setFontName("C39HrP36DlTt");
                        short size = 720;
                        font.setFontHeight(size);
                    }else if (model.getValueAt(rows, cols).toString().length()==15) {
                        font.setFontName("Libre Barcode EAN13 Text");
                        short size = 900;
                        font.setFontHeight(size);
                    }
                    CellStyle style = wb.createCellStyle();
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    row.getCell(cols).setCellStyle(style);
                }else{
                    CellStyle style = wb.createCellStyle();
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    row.getCell(cols).setCellStyle(style);
                }
                row.setHeight((short)-1);
            }
            row = sheet2.createRow(rows+3);
        }

        Sheet sheet3 = wb.createSheet("Sin existencias");
        row = sheet3.createRow(2);
        model = BadInventory.getModel();

        headerRow = sheet3.createRow(0);
        for (int headings = 0; headings<model.getColumnCount(); headings++){
            headerRow.createCell(headings).setCellValue(model.getColumnName(headings));
        }
        for (int rows = 0; rows<model.getRowCount(); rows++){
            for(int cols = 0; cols < BadInventory.getColumnCount(); cols++){
                row.createCell(cols).setCellValue(model.getValueAt(rows, cols).toString());
                if (cols == 4){
                    org.apache.poi.ss.usermodel.Font font = wb.createFont();
                    if (model.getValueAt(rows, cols).toString().length()<=5) {
                        font.setFontName("C39HrP36DlTt");
                        short size = 720;
                        font.setFontHeight(size);
                    }else if (model.getValueAt(rows, cols).toString().length()==15) {
                        font.setFontName("Libre Barcode EAN13 Text");
                        short size = 900;
                        font.setFontHeight(size);
                    }
                    CellStyle style = wb.createCellStyle();
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    row.getCell(cols).setCellStyle(style);
                }else{
                    CellStyle style = wb.createCellStyle();
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    row.getCell(cols).setCellStyle(style);
                }
                row.setHeight((short)-1);
            }
            row = sheet3.createRow(rows+3);
        }

        // make the column size according to content
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet2.autoSizeColumn(0);
        sheet2.autoSizeColumn(1);
        sheet2.autoSizeColumn(2);
        sheet2.autoSizeColumn(3);
        sheet2.autoSizeColumn(4);
        sheet3.autoSizeColumn(0);
        sheet3.autoSizeColumn(1);
        sheet3.autoSizeColumn(2);
        sheet3.autoSizeColumn(3);
        sheet3.autoSizeColumn(4);

        // creates a directory to save files if it doesn't exist
        if (Files.notExists(Path.of(System.getProperty("user.dir") + "/Reportes/Excel"))){
            Files.createDirectory(Path.of(System.getProperty("user.dir") + "/Reportes/Excel"));
        }

        // creates the Excel file
        String path = System.getProperty("user.dir")+"/Reportes/Excel/Inventario "+ LocalDate.now().toString()+".xlsx";
        wb.write(new FileOutputStream(path));
        JOptionPane.showMessageDialog(null, "El inventario se descargó correctamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }

    // extracts all products from DB and show them in the GUI
    public void showInventory(JTable GoodInventory, JTable MediumInventory, JTable BadInventory) throws IOException, FontFormatException {
        productsListGood = new ArrayList<Product>();
        productsListMedium = new ArrayList<Product>();
        productsListBad = new ArrayList<Product>();

        searchAllProducts(GoodInventory,MediumInventory,BadInventory);

        // make a cell renderer for cells where there isn't a barcode
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

        // make a cell renderer for cells where there is a barcode
        DefaultTableCellRenderer rCode = new DefaultTableCellRenderer() {
            java.awt.Font BarCode = Font.createFont(Font.TRUETYPE_FONT, new File("src/res/Codebar Font/font.ttf")).deriveFont(35f);
            java.awt.Font BarCode13 = Font.createFont(Font.TRUETYPE_FONT, new File("src/res/Codebar Font/font2.ttf")).deriveFont(55f);
            java.awt.Font font = BarCode;
            java.awt.Font font13 = BarCode13;

            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value.toString().length()<=5) {
                    renderer.setFont(font);
                } else if (value.toString().length()==15) {
                    renderer.setFont(font13);
                }

                renderer.setHorizontalAlignment(CENTER);

                return renderer;
            }
        };

        // fills the GUI tables with the content of the global variables
        if (productsListGood.size()!=0){
            for (int i = 0; i <= 3; i++) {
                GoodInventory.getColumnModel().getColumn(i).setCellRenderer(r);
            }
            GoodInventory.getColumnModel().getColumn(4).setCellRenderer(rCode);
            GoodInventory.setRowHeight(60);
        }
        if (productsListMedium.size()!=0){
            for (int i = 0; i <= 3; i++) {
                MediumInventory.getColumnModel().getColumn(i).setCellRenderer(r);
            }
            MediumInventory.getColumnModel().getColumn(4).setCellRenderer(rCode);
            MediumInventory.setRowHeight(60);
        }
        if (productsListBad.size()!=0){
            for (int i = 0; i <= 3; i++) {
                BadInventory.getColumnModel().getColumn(i).setCellRenderer(r);
            }
            BadInventory.getColumnModel().getColumn(4).setCellRenderer(rCode);
            BadInventory.setRowHeight(60);
        }
    }
}
