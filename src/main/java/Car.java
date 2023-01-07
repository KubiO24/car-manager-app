import com.fasterxml.uuid.Generators;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class Car {
    private Integer id;
    private UUID uuid;
    private String model;
    private Integer year;
    private ArrayList<Airbag> airbags = new ArrayList<>();
    private String color;
    private Boolean invoiceGenerated;


    public void generateId(Integer id) {
        this.id = id;
        this.uuid = Generators.randomBasedGenerator().generate();
        this.invoiceGenerated = false;
    }

    public Boolean uuidEquals(String uuid) {
        if (this.uuid.toString().equals(uuid)) {
            return true;
        }
        return false;
    }

    public void edit(String model, Integer year) {
        this.model = model;
        this.year = year;
    }

    public void generateInvoice() {
        Document document = new Document(); // dokument pdf
        String path = "invoices/" + this.uuid + ".pdf"; // lokalizacja zapisu
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk("tekst", font); // akapit

        try {
            document.add(chunk);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        document.close();
        this.invoiceGenerated = true;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", airbags=" + airbags +
                ", color='" + color + '\'' +
                ", invoiceGenerated=" + invoiceGenerated +
                '}';
    }
}

class Airbag {
    String description;
    boolean value;
}


class UpdateCar {
    String uuid;
    String model;
    String year;
}
