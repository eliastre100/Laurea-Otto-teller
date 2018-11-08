package models;

import anotations.Attribute;
import anotations.Model;
import utils.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ModelBase {

    @Attribute(name="id")
    protected int id = -1;

    private HashMap<String, Pair<Field, Class>> attributes = new HashMap<>();
    private String table = "";

    ModelBase() {
        System.out.println("[INFO] Initializing model " + this.getClass().toString());
        this.initTable();
        this.initAttributes();
    }

    public void importDatabaseData(ResultSet res) {
        this.fromDatabase(res);
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
                attributes.put(annotation.name(), new Pair<>(field, field.getType()));
                System.out.println("[INFO] Attribute " + annotation.name() + " found on " + field.getName() + " (" + field.getType().toString() + ")");
            }
        }
    }

    private void fromDatabase(ResultSet data) {
        try {
            for(Map.Entry<String, Pair<Field, Class>> entry : this.attributes.entrySet()) {
                int idx = data.findColumn(entry.getKey());
                this.setValueFromDb(entry.getValue().left, this.getValueFromDatabase(data, idx, entry.getValue().right));
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Invalid ResultSet provided to model " + this.getClass().getSimpleName() +
                    ". " + e.getMessage() +
                    " Aborting database importation.");
        }
    }

    private <type> type getValueFromDatabase(ResultSet data, int idx, Class clazz) {
        try {
            Method get = data.getClass().getMethod("get" + clazz.getSimpleName(), int.class);
            return (type) get.invoke(data, idx);
        } catch (NoSuchMethodException e) {
            System.err.println("[WARNING] No method found for type " + clazz.getSimpleName() + " (get" + clazz.getSimpleName() + ")");
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error");
            e.printStackTrace();
        }
        return null;
    }

    private <valueType > void setValueFromDb(Field field, valueType value) {
        boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            field.set(this, value);
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error");
            e.printStackTrace();
        } finally {
            field.setAccessible(accessible);
        }
    }
}
