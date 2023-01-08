import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Invoices {
    public static Invoice generateInvoice(ArrayList<Car> carList) {
        return new Invoice("Invoice for all cars", "all_cars", "buyer", "seller", carList);
    }

    public static Invoice generateInvoiceByYear(Integer year, ArrayList<Car> carList) {
        Stream<Car> stream = carList.stream().filter(car -> Objects.equals(car.getYear(), year));
        List<Car> list = stream.collect(Collectors.toList());
        carList = new ArrayList<Car>(list);
        return new Invoice("Invoice for cars made in " + year, "all_cars_by_year", "buyer", "seller", carList);
    }

    public static Invoice generateInvoiceByPrice(Integer min, Integer max, ArrayList<Car> carList) {
        Stream<Car> stream = carList.stream().filter(car -> car.getPrice() >= min && car.getPrice() <= max);
        List<Car> list = stream.collect(Collectors.toList());
        carList = new ArrayList<Car>(list);
        return new Invoice("Invoice for cars in price range: $" + min + " - $" + max, "all_cars_by_price", "buyer", "seller", carList);
    }
}
