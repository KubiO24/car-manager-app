import com.fasterxml.uuid.Generators;
import com.google.gson.Gson;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Car {
    private Integer id;
    private UUID uuid;
    private String model;
    private Integer year;
    private ArrayList<Airbag> airbags = new ArrayList<>();
    private String color;
    private CustomDate dateOfPurchase;
    private Integer price;
    private Integer tax;
    private Boolean invoiceGenerated;
    private ArrayList<String> imagesList = new ArrayList<>();


    public Car(Integer id) {
        Random random = new Random();

        this.id = id;
        this.uuid = Generators.randomBasedGenerator().generate();

        String[] models = {"BMW", "Fiat", "Mercedes", "Peugot"};
        this.model = models[random.nextInt(models.length)];

        int[] years = {2000, 2010, 2020};
        this.year = years[random.nextInt(years.length)];

        this.airbags.add(new Airbag("kierowca"));
        this.airbags.add(new Airbag("pasażer"));
        this.airbags.add(new Airbag("kanapa"));
        this.airbags.add(new Airbag("boczne"));

        String[] colors = {"#000000", "#808080", "#FF0000", "#008000", "#FFA500", "#0000FF"};
        this.color = colors[random.nextInt(colors.length)];

        int day = ThreadLocalRandom.current().nextInt(1, 31 + 1);
        int month = ThreadLocalRandom.current().nextInt(1, 12 + 1);
        int year = ThreadLocalRandom.current().nextInt(2000, 2022 + 1);
        this.dateOfPurchase = new CustomDate(day, month, year);

        this.price = ThreadLocalRandom.current().nextInt(10000, 100000);

        Integer[] tax = {0, 7, 22};
        this.tax = tax[random.nextInt(tax.length)];

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

        HashMap<String, BaseColor> colorMap = new HashMap<>() {{
            put("black", BaseColor.BLACK);
            put("gray", BaseColor.GRAY);
            put("red", BaseColor.RED);
            put("green", BaseColor.GREEN);
            put("orange", BaseColor.ORANGE);
            put("blue", BaseColor.BLUE);
        }};

        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Font bigFont = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
        Font boldFont = FontFactory.getFont(FontFactory.COURIER, 16, Font.BOLD, BaseColor.BLACK);

        String colorText = switch (this.color) {
            case "#000000" -> "black";
            case "#808080" -> "gray";
            case "#FF0000" -> "red";
            case "#008000" -> "green";
            case "#FFA500" -> "orange";
            case "#0000FF" -> "blue";
            default -> "custom";
        };

        BaseColor baseColor;
        if(colorText.equals("custom")) {
            colorText = this.color;
            int r = Integer.valueOf(this.color.substring(1, 3), 16);
            int g = Integer.valueOf(this.color.substring(3, 5), 16);
            int b = Integer.valueOf(this.color.substring(5, 7), 16);
            baseColor = new BaseColor(r, g, b);
        }else {
            baseColor = colorMap.get(colorText);
        }
        Font coloredFont = FontFactory.getFont(FontFactory.COURIER, 16, baseColor);

        Paragraph title = new Paragraph("INVOICE for: " + this.uuid, boldFont);
        Paragraph model = new Paragraph("model: " + this.model, bigFont);
        Paragraph color = new Paragraph("color: " + colorText, coloredFont);
        Paragraph year = new Paragraph("year: " + this.year, font);
        Paragraph airbag1 = new Paragraph("airbag: " + this.airbags.get(0).description + " - " + this.airbags.get(0).value, font);
        Paragraph airbag2 = new Paragraph("airbag: " + this.airbags.get(1).description + " - " + this.airbags.get(1).value, font);
        Paragraph airbag3 = new Paragraph("airbag: " + this.airbags.get(2).description + " - " + this.airbags.get(2).value, font);
        Paragraph airbag4 = new Paragraph("airbag: " + this.airbags.get(3).description + " - " + this.airbags.get(3).value, font);

        try {
            document.add(title);
            document.add(model);
            document.add(color);
            document.add(year);
            document.add(airbag1);
            document.add(airbag2);
            document.add(airbag3);
            document.add(airbag4);
            String[] models = {"BMW", "Fiat", "Mercedes", "Peugot"};
            if(Arrays.asList(models).contains(this.model)) {
                Image img = Image.getInstance("src/main/resources/public/car brands images/" + this.model + ".png");
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / img.getWidth()) * 100;
                img.scalePercent(scaler);
                document.add(img);
            }
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }

        document.close();
        this.invoiceGenerated = true;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getTax() {
        return tax;
    }

    public void addImage(String imageName) {
        imagesList.add(imageName);
    }

    public String getImages() {
        Gson gson = new Gson();
        return gson.toJson(imagesList);
    }

    public void deleteImage(String name) {
        imagesList.remove(name);
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
                ", dateOfPurchase=" + dateOfPurchase +
                ", price=" + price +
                ", tax=" + tax +
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
