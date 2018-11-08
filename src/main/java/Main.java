import models.Car;
import repositories.CarRepository;

import java.util.List;

public class Main {

    public static void main(String[] attr) {
        CarRepository repository = new CarRepository();
        List<Car> cars = repository.findAll();
        for (Car car : cars) {
            System.out.println(car);
        }
    }

}
