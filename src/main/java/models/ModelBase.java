package models;

import anotations.Attribute;
import anotations.Model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ModelBase {

    @Attribute(name="id")
    protected int id;

    private HashMap<String, Class> attributes = new HashMap<>();
    private String table = "";

    ModelBase() {
        System.out.println("Initializing model " + this.getClass().toString());
        this.initTable();
        this.initAttributes();
    }

    public void importDatabaseData(ResultSet res) {
        System.out.println("TODO Implement modelbase from database");
    }

    public void findAll() {
        String request = "SELECT * FROM " + this.table + ";";
        System.out.println("Running request \"" + request + "\"");
    }

    private void initTable() {
        Model model = this.getClass().getAnnotation(Model.class);
        if (model != null) {
            this.table = model.table();
        }
    }

    private void initAttributes() {
        for (Field field : this.getClass().getDeclaredFields()) {
            Attribute annotation = field.getAnnotation(Attribute.class);
            if (annotation != null) {
                attributes.put(annotation.name(), field.getType());
                System.out.println("Attribute " + annotation.name() + " found on " + field.getName() + " (" + field.getType().toString() + ")");
            }
        }
    }

    private void fromDatabase(ResultSet data) {
        for(Map.Entry<String, Class> entry : this.attributes.entrySet()) {
            System.out.println("Field " + entry.getKey());
        }
    }
}
