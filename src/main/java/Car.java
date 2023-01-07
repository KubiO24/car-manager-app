import com.fasterxml.uuid.Generators;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Car {
    private Integer id;
    private UUID uuid;
    private String model;
    private Integer year;
    private ArrayList<Airbag> airbags = new ArrayList<>();
    private String color;
    private Boolean invoiceGenerated;


    public Car(Integer id) {
        this.id = id;
        this.uuid = Generators.randomBasedGenerator().generate();

        String[] models = {"BMW", "Fiat", "Mercedes", "Peugot"};
        Random random = new Random();
        int index = random.nextInt(models.length);
        this.model = models[index];

        int[] years = {2000, 2010, 2020};
        index = random.nextInt(years.length);
        this.year = years[index];

        this.airbags.add(new Airbag("kierowca"));
        this.airbags.add(new Airbag("pasażer"));
        this.airbags.add(new Airbag("kanapa"));
        this.airbags.add(new Airbag("boczne"));

        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = random.nextInt(0xffffff + 1);
        // format it as hexadecimal string (with hashtag and leading zeros)
        this.color = String.format("#%06x", nextInt);

        this.invoiceGenerated = false;
    }


    public void generateId(Integer id) {
        this.id = id;
        this.uuid = Generators.randomBasedGenerator().generate();
        this.invoiceGenerated = false;
    }

    public Boolean uuidEquals(String uuid) {
        return this.uuid.toString().equals(uuid);
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
        } catch (DocumentException | FileNotFoundException e) {
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
    public Airbag(String description) {
        this.description = description;
        Random random = new Random();
        this.value = random.nextBoolean();
    }

    String description;
    boolean value;
}


class UpdateCar {
    String uuid;
    String model;
    String year;
}
