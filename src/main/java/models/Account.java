package models;

import anotations.Attribute;
import anotations.Model;

@Model(table = "cars")
public class Account extends ModelBase {

    @Attribute(name = "name")
    private String name;
}
