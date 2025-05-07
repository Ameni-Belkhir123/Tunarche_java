package TunArche.services;

import TunArche.entities.Billet;
import TunArche.entities.Event;
import TunArche.tools.MyConnection;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.barcodes.Barcode128;
import java.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class PDFGenerator {

    private static final Connection cnx = MyConnection.getInstance().getCnx();
    private static final EventImpl eventService = new EventImpl();

    public static File generateBilletPDF(Billet billet) throws IOException {
        // Create the PDF file
        File pdfFile = new File("Billet_" + billet.getNumero() + ".pdf");
        PdfWriter writer = new PdfWriter(pdfFile);
        PdfDocument pdf = new PdfDocument(writer);

        // Set page size to a ticket-like size (e.g., 3.5in x 8in)
        Document document = new Document(pdf, new PageSize(252, 576)); // 3.5in x 8in (72 points per inch)
        document.setMargins(20, 20, 20, 20);

        // Add a decorative border around the ticket
        PdfCanvas canvas = new PdfCanvas(pdf.addNewPage());
        canvas.setStrokeColor(ColorConstants.BLACK)
                .setLineWidth(2)
                .rectangle(15, 15, 252 - 30, 576 - 30) // Adjusted for margins
                .stroke();

        // Header: Event Name or Branding
        Paragraph header = new Paragraph()
                .add(new Text("TunArche Event Ticket\n").setFontSize(18).setBold().setFontColor(ColorConstants.DARK_GRAY))
                .add(new Text("Official Admission Pass").setFontSize(10).setFontColor(ColorConstants.GRAY))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(header);

        // Horizontal line separator
        document.add(new Paragraph("--------------------------------------------------")
                .setFontSize(8)
                .setFontColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        // Fetch the event name using EventImpl
        String eventName = "Unknown Event";
        Event event = eventService.getById(billet.getEventId());
        if (event != null && event.getNameEvent() != null) {
            eventName = event.getNameEvent();
        }

        // Fetch the buyer name from the user table
        String buyerName = "Unknown Buyer";
        String sqlUser = "SELECT name FROM user WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sqlUser)) {
            pst.setInt(1, billet.getBuyerId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                buyerName = rs.getString("name");
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération nom utilisateur : " + e.getMessage());
        }

        // Ticket Details in a Table
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(10);

        // Add ticket details as table rows
        table.addCell(new Paragraph("Ticket Number").setFontSize(10).setBold());
        table.addCell(new Paragraph(String.valueOf(billet.getNumero())).setFontSize(10));

        table.addCell(new Paragraph("Type").setFontSize(10).setBold());
        table.addCell(new Paragraph(billet.getType()).setFontSize(10));

        // Format the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = event.getDateEnd().format(formatter);
        table.addCell(new Paragraph("Issue Date").setFontSize(10).setBold());
        table.addCell(new Paragraph(formattedDate).setFontSize(10));

        table.addCell(new Paragraph("Payment Method").setFontSize(10).setBold());
        table.addCell(new Paragraph(billet.getModePaiement()).setFontSize(10));

        // Display event name instead of event ID
        table.addCell(new Paragraph("Event").setFontSize(10).setBold());
        table.addCell(new Paragraph(eventName).setFontSize(10));

        // Display buyer name instead of buyer ID
        table.addCell(new Paragraph("Buyer").setFontSize(10).setBold());
        table.addCell(new Paragraph(buyerName).setFontSize(10));
if(billet.getType()=="Standard"){
        table.addCell(new Paragraph("price").setFontSize(10).setBold());
        table.addCell(new Paragraph(event.getPrice() + " €").setFontSize(10));}
        if(billet.getType()=="Premium"){
            table.addCell(new Paragraph("price").setFontSize(10).setBold());
            table.addCell(new Paragraph((event.getPrice()*2) + " €").setFontSize(10));}
        if(billet.getType()=="VIP"){
            table.addCell(new Paragraph("price").setFontSize(10).setBold());
            table.addCell(new Paragraph((event.getPrice()*3) + " €").setFontSize(10));}

        // Remove table borders for a cleaner look
        table.setBorder(Border.NO_BORDER);
        document.add(table);

        // Add a Barcode for authenticity (using Barcode128)
        Barcode128 barcode = new Barcode128(pdf);
        barcode.setCode("TICKET-" + billet.getNumero());
        barcode.setCodeType(Barcode128.CODE128);
        PdfFormXObject barcodeXObject = barcode.createFormXObject(pdf);
        com.itextpdf.layout.element.Image barcodeImage = new com.itextpdf.layout.element.Image(barcodeXObject)
                .setWidth(150)
                .setHeight(30)
                .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
        document.add(barcodeImage);

        // Barcode label
        document.add(new Paragraph("Scan to Verify: TICKET-" + billet.getNumero())
                .setFontSize(8)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        // Footer: Terms or Contact Info
        Paragraph footer = new Paragraph()
                .add(new Text("Thank you for your purchase!\n").setFontSize(8).setFontColor(ColorConstants.GRAY))
                .add(new Text("Contact: support@tunarche.com | Terms: Non-refundable").setFontSize(8).setFontColor(ColorConstants.GRAY))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);
        document.add(footer);

        // Close the document
        document.close();

        return pdfFile;
    }
}