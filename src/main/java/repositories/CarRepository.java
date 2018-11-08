package repositories;

import anotations.Repository;
import models.Car;

@Repository(model = Car.class)
public class CarRepository extends RepositoryBase{
}
