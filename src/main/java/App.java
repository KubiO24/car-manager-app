import static spark.Spark.*;

import com.fasterxml.uuid.Generators;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

class App {
    private static Integer id = 1;
    private static ArrayList<Car> cars = new ArrayList<>();

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
        String uuid = req.body().toString();
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
        return true;
    }

    static Boolean invoice(Request req, Response res) {
        return true;
    }

    static Boolean invoices(Request req, Response res) {
        return true;
    }

    static Boolean image(Request req, Response res) throws IOException {
        res.type("image/jpeg");

        OutputStream outputStream = res.raw().getOutputStream();

        outputStream.write(Files.readAllBytes(Path.of("images/" + req.queryParams("name"))));
        outputStream.flush();
        System.out.println(req.queryParams("name"));
        return true;
    }

    static String imageOperation(Request req, Response res) {
        Gson gson = new Gson();
        res.type("application/json");

        System.out.println(req.body());

        ArrayList<Integer> data = new ArrayList<>();
        data.add(100);

        return gson.toJson(data);
    }
}