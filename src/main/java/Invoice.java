import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class Invoice {
    private String nr;
    private String title;
    private String buyer;
    private String seller;
    private ArrayList<Car> carList;
    private Date date;
    private String name;

    public Invoice(String title, String type, String buyer, String seller, ArrayList<Car> carList) {
        this.title = title;
        this.buyer = buyer;
        this.seller = seller;
        this.carList = carList;
        this.date = new Date(System.currentTimeMillis());
        this.generateNr();
        this.generateName(type);
        this.generateInvoice();
    }

    private void generateNr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
        this.nr = "TAX/" + sdf.format(this.date);
    }

    private void generateName(String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        this.name = "invoice_" + type + "_" + sdf.format(this.date);
    }

    private void generateInvoice() throws RuntimeException {
        Document document = new Document();
        String path = "invoices/" + this.name + ".pdf";
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
        } catch (DocumentException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        document.open();

        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Font boldFont = FontFactory.getFont(FontFactory.COURIER, 18, Font.BOLD, BaseColor.BLACK);
        Font redFont = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.RED);

        try {
            Paragraph title = new Paragraph("INVOICE: " + this.nr, boldFont);
            document.add(title);

            Paragraph buyer = new Paragraph("buyer: " + this.buyer, font);
            document.add(buyer);

            Paragraph seller = new Paragraph("seller: " + this.seller, font);
            document.add(seller);

            Paragraph type = new Paragraph(this.title, redFont);
            document.add(type);

            Paragraph space = new Paragraph(" ", redFont);
            document.add(space);

            PdfPTable table = new PdfPTable(5);
            ArrayList<String> header = new ArrayList<String>();
            header.add("index");
            header.add("year");
            header.add("price");
            header.add("tax");
            header.add("value");

            header.forEach((value -> {
                PdfPCell cell = new PdfPCell(new Phrase(value, font));
                table.addCell(cell);
            }));

            AtomicReference<Integer> index = new AtomicReference<>(1);
            AtomicReference<Double> sumPrice = new AtomicReference<>(0.0);

            this.carList.forEach((car -> {
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(String.valueOf(index), font));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(car.getYear()), font));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("$" + Double.valueOf(car.getPrice()), font));
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(car.getTax() + "%", font));
                table.addCell(cell);

                Double taxValue = Double.valueOf(car.getPrice()) * Double.valueOf(car.getTax()) / 100;
                String value = String.valueOf(car.getPrice() + taxValue);
                cell = new PdfPCell(new Phrase("$" + value, font));
                table.addCell(cell);

                index.updateAndGet(v -> v + 1);
                sumPrice.updateAndGet(v -> v + car.getPrice());
            }));
            document.add(table);

            Paragraph sum = new Paragraph("TOTAL: " + sumPrice, boldFont);
            document.add(sum);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        document.close();
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "title='" + title + '\'' +
                ", date=" + date +
                ", name='" + name + '\'' +
                '}';
    }
}
