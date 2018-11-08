package models;

import anotations.Attribute;
import anotations.Model;

@Model(table = "cars")
public class Car extends ModelBase{

    @Attribute(name = "plateNr")
    protected String plateNr;

    @Attribute(name = "colour")
    protected String colour;

    @Attribute(name = "model")
    protected String model;

    @Attribute(name = "year")
    protected String year;

    public String toString() {
        return "Car " + plateNr + " of color " + colour + " is " + model + " built in " + year;
    }
}
