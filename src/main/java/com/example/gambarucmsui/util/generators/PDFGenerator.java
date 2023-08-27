package com.example.gambarucmsui.util.generators;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFGenerator {
    public static byte[] generatePDF(List<BarcodeView> images) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            int numColumns = 4;

            PdfPTable mainTable = new PdfPTable(numColumns);
            mainTable.setWidthPercentage(100);

            for (BarcodeView image : images) {
                // Create a unit table
                PdfPTable unitTable = createUnitTable(image);
                PdfPCell unitCell = new PdfPCell(unitTable);
                unitCell.setBorderWidth(1f);

                mainTable.addCell(unitCell);
            }


            // Add empty cells to fill the remaining columns
            int remainingCells = numColumns - (images.size() % numColumns);
            for (int i = 0; i < remainingCells; i++) {
                PdfPCell emptyCell = new PdfPCell();
                emptyCell.setBorder(PdfPCell.NO_BORDER);
                mainTable.addCell(emptyCell);
            }

            document.add(mainTable);
            document.close();

            System.out.println("PDF generated successfully.");
        } catch (DocumentException e) {
            System.out.println("PDF generation failed. Exception: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
    }

    private static PdfPTable createUnitTable(BarcodeView image) throws DocumentException, IOException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        // First row: Image
        Image pdfImage = Image.getInstance(image.getBufferedImage(), null);
        PdfPCell imageCell = new PdfPCell();
        imageCell.addElement(pdfImage);
        imageCell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(imageCell);

        // Second row: Barcode text
        PdfPCell textCell = new PdfPCell();
        Paragraph elements = new Paragraph(image.getBarcodeText());
        elements.setAlignment(Paragraph.ALIGN_CENTER);
        textCell.addElement(elements);
        textCell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(textCell);

        return table;
    }
}
