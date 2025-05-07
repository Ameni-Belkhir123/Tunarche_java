package TunArche.entities;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PdfGenerator {

    public static void generateUserCV(user u) {
        String html = getHtmlForUser(u); // HTML + CSS
        String fileName = "cv_" + u.getName() + "_" + u.getLastName() + ".pdf";

        try (OutputStream os = new FileOutputStream(fileName)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();

            System.out.println("PDF créé : " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void openPdfFile(String fileName) {
        try {
            // Check if Desktop is supported on the current platform
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                File pdfFile = new File(fileName);
                if (pdfFile.exists()) {
                    Desktop.getDesktop().open(pdfFile);
                    System.out.println("PDF ouvert automatiquement : " + fileName);
                } else {
                    System.err.println("Le fichier PDF n'existe pas : " + fileName);
                }
            } else {
                System.err.println("L'ouverture automatique du PDF n'est pas prise en charge sur cette plateforme.");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getHtmlForUser(user u) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body {
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    margin: 0;
                    padding: 0;
                    background: #f4f4f4;
                    color: #333;
                }
                .cv-container {
                    width: 80%%;
                    max-width: 800px;
                    margin: 30px auto;
                    background: white;
                    padding: 30px;
                    border-radius: 12px;
                    box-shadow: 0 0 15px rgba(0,0,0,0.1);
                }
                .header {
                    text-align: center;
                    border-bottom: 2px solid #4CAF50;
                    padding-bottom: 10px;
                    margin-bottom: 20px;
                }
                .header h1 {
                    margin: 0;
                    font-size: 32px;
                    color: #4CAF50;
                }
                .header p {
                    margin: 5px 0;
                    font-size: 16px;
                    color: #555;
                }
                .section {
                    margin-bottom: 20px;
                }
                .section h2 {
                    font-size: 22px;
                    color: #333;
                    border-bottom: 1px solid #ccc;
                    padding-bottom: 5px;
                    margin-bottom: 10px;
                }
                .section ul {
                    list-style: none;
                    padding-left: 0;
                }
                .section ul li {
                    margin-bottom: 5px;
                    font-size: 15px;
                }
            </style>
        </head>
        <body>
            <div class="cv-container">
                <div class="header">
                    <h1>%s %s</h1>
                    <p>Email: %s | Phone: %s</p>
                </div>
                <div class="section">
                    <h2>Profile</h2>
                   
                </div>
            </div>
        </body>
        </html>
        """.formatted(u.getName(), u.getLastName(), u.getEmail(), u.getPhone());
    }
}
