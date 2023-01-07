import com.fasterxml.uuid.Generators;

import java.util.ArrayList;
import java.util.UUID;

public class Car {
    private Integer id;
    private UUID uuid;
    private String model;
    private Integer year;
    private ArrayList<Airbag> airbags = new ArrayList<>();
    private String color;


    public void generateId(Integer id) {
        this.id = id;
        this.uuid = Generators.randomBasedGenerator().generate();
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

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", airbags=" + airbags +
                ", color='" + color + '\'' +
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
