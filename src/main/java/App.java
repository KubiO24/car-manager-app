import static spark.Spark.*;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

class App {
    private static Integer id = 1;
    private static final ArrayList<Car> cars = new ArrayList<>();
    private static final ArrayList<Invoice> invoices = new ArrayList<>();
    private static final ArrayList<Invoice> invoices_year = new ArrayList<>();
    private static final ArrayList<Invoice> invoices_price = new ArrayList<>();
    private static String selectedGalleryUuid = "";

    public static void main(String[] args) {
//        staticFiles.location("/public");

        String projectDir = System.getProperty("user.dir");
        String staticDir = "/src/main/resources/public";
        staticFiles.externalLocation(projectDir + staticDir);

        get("/json", (req, res) -> json(req, res)); // pobranie danych json

        post("/add", (req, res) -> add(req, res)); // dodanie auta
        post("/delete", (req, res) -> delete(req, res)); // usunięcie auta
        post("/update", (req, res) -> update(req, res)); // edycja auta

        post("/generate", (req, res) -> generate(req, res)); // generowanie bazy aut
        post("/invoice", (req, res) -> invoice(req, res)); // generowanie faktury
        get("/invoices", (req, res) -> invoices(req, res)); // pobranie faktury

        post("/generateInvoice", (req, res) -> generateInvoice(req, res));
        post("/generateInvoiceYear", (req, res) -> generateInvoiceYear(req, res));
        post("/generateInvoicePrice", (req, res) -> generateInvoicePrice(req, res));
        get("/invoicesJson", (req, res) -> invoicesJson(req, res));
        get("/invoicesYearJson", (req, res) -> invoicesYearJson(req, res));
        get("/invoicesPriceJson", (req, res) -> invoicesPriceJson(req, res));

        post("/setGalleryUuid", (req, res) -> setGalleryUuid(req, res));
        get("/getGalleryImages", (req, res) -> getGalleryImages(req, res));
        post("/upload", (req, res) -> upload(req, res));
        get("/thumb", (req, res) -> thumb(req, res));
        post("/deleteImage", (req, res) -> deleteImage(req, res));

        get("/image", (req, res) -> image(req, res));
        post("/imageOperation", (req, res) -> imageOperation(req, res));
    }

    static String add(Request req, Response res) {
        Gson gson = new Gson();
        Car car = gson.fromJson(req.body(), Car.class);
        car.generateId(id);
        id++;
        cars.add(car);

        res.type("application/json");
        return gson.toJson(car);
    }

    static String json(Request req, Response res) {
        Gson gson = new Gson();
        res.type("application/json");
        return gson.toJson(cars);
    }

    static Boolean delete(Request req, Response res) {
        String uuid = req.body();
        cars.removeIf(car -> car.uuidEquals(uuid));
        return true;
    }

    static Boolean update(Request req, Response res) {
        Gson gson = new Gson();
        UpdateCar data = gson.fromJson(req.body(), UpdateCar.class);

        cars.forEach((car) -> {
            if(car.uuidEquals(data.uuid)) {
                car.edit(data.model, Integer.valueOf(data.year));
            }
        });
        return true;
    }

    static Boolean generate(Request req, Response res) {
        for (int i = 0; i < 5; i++) {
            Car car = new Car(id);
            id++;
            cars.add(car);
        }
        return true;
    }

    static Boolean invoice(Request req, Response res) {
        cars.forEach((car) -> {
            if(car.uuidEquals(req.body())) {
                car.generateInvoice();
            }
        });
        return true;
    }

    static Boolean invoices(Request req, Response res) {
        res.type("application/octet-stream");
        res.header("Content-Disposition", "attachment; filename=" + req.queryParams("name") + ".pdf"); // nagłówek

        OutputStream  outputStream;
        try {
            outputStream = res.raw().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            outputStream.write(Files.readAllBytes(Path.of("invoices/" + req.queryParams("name") + ".pdf"))); // response pliku do przeglądarki
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    static Boolean generateInvoice(Request req, Response res) {
        invoices.add(Invoices.generateInvoice(cars));
        return true;
    }

    static Boolean generateInvoiceYear(Request req, Response res) {
        invoices_year.add(Invoices.generateInvoiceByYear(2000, cars));
        return true;
    }

    static Boolean generateInvoicePrice(Request req, Response res) {
        Gson gson = new Gson();
        PriceRange priceRange = gson.fromJson(req.body(), PriceRange.class);
        invoices_price.add(Invoices.generateInvoiceByPrice(priceRange.min, priceRange.max, cars));
        return true;
    }

    static String invoicesJson(Request req, Response res) {
        Gson gson = new Gson();
        res.type("application/json");
        return gson.toJson(invoices);
    }

    static String invoicesYearJson(Request req, Response res) {
        Gson gson = new Gson();
        res.type("application/json");
        return gson.toJson(invoices_year);
    }

    static String invoicesPriceJson(Request req, Response res) {
        Gson gson = new Gson();
        res.type("application/json");
        return gson.toJson(invoices_price);
    }

    static Boolean setGalleryUuid(Request req, Response res) {
        selectedGalleryUuid = req.body();
        return true;
    }

    static String getGalleryImages(Request req, Response res) {
        res.type("application/json");

        final String[] imagesList = {""};
        cars.forEach((car) -> {
            if(car.uuidEquals(selectedGalleryUuid)) {
                imagesList[0] = car.getImages();
            }
        });

        Gson gson = new Gson();
        return gson.toJson(imagesList[0].toString());
    }

    static String upload(Request req, Response res) throws ServletException, IOException {
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/images"));
        for(Part p : req.raw().getParts()){
            InputStream inputStream = p.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            String fileName = p.getName();

            // if file with this name exists add " copy" at the end of its name
            while(new File("images/" + fileName).isFile()) {
                String[] arr = fileName.split("\\.");
                Integer index = arr[arr.length-1].length() + 1;
                fileName = new StringBuilder(fileName).insert(fileName.length()-index, " copy").toString();
            }
            String finalFileName = fileName;
            cars.forEach((car) -> {
                if(car.uuidEquals(selectedGalleryUuid)) {
                    car.addImage(finalFileName);
                }
            });
            FileOutputStream fos = new FileOutputStream("images/" + fileName);
            fos.write(bytes);
            fos.close();
            // dodaj do Arraylist z nazwami aut do odesłania do przeglądarki
        }

        return "saved";
    }

    static String thumb(Request req, Response res) throws IOException {
        res.type("image/jpeg");

        String path = "images/" + req.queryParams("id");

        OutputStream outputStream = null;
        outputStream = res.raw().getOutputStream();

        outputStream.write(Files.readAllBytes(Path.of(path)));
        outputStream.flush();

        return "ok";
    }

    static String deleteImage(Request req, Response res) {
        cars.forEach((car) -> {
            if(car.uuidEquals(selectedGalleryUuid)) {
                car.deleteImage(req.body());
            }
        });
        return "ok";
    }

    static Boolean image(Request req, Response res) throws IOException {
        res.type("image/jpeg");

        OutputStream outputStream = res.raw().getOutputStream();

        outputStream.write(Files.readAllBytes(Path.of("images/" + req.queryParams("id"))));
        outputStream.flush();
        return true;
    }

    static String imageOperation(Request req, Response res) {
        Gson gson = new Gson();
        res.type("application/json");

        ArrayList<Integer> data = new ArrayList<>();
        data.add(100);

        return gson.toJson(data);
    }
}

class PriceRange {
    Integer min;
    Integer max;
}