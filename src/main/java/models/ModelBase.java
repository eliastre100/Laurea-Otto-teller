package models;

import anotations.Attribute;
import anotations.Model;
import utils.DatabaseProvider;
import utils.Pair;
import utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
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

    public String toString() {
        try {
            StringBuilder res = new StringBuilder("[Model " + this.getClass().getSimpleName() + "(" + this.id + "): {");
            for (Map.Entry<String, Pair<Field, Class>> entry : this.attributes.entrySet()) {
                res.append(entry.getValue().left.getName()).append(": ").append(entry.getValue().left.get(this)).append(", ");

            }
            return res.substring(0, res.length() - 2) + "}]";
        } catch (IllegalAccessException e) {
            return "[Model " + this.getClass().getSimpleName() + "(" + this.id + "): {FAILED TO RETRIEVE FIELDS}]";
        }
    }

    public void importDatabaseData(ResultSet res) {
        this.fromDatabase(res);
    }

    public boolean save() {
        if (this.id == -1) {
            return this.persist();
        } else {
            return this.update();
        }
    }

    public int getId() {
        return this.id;
    }

    public void setId(Integer id) {
        if (id != null) {
            this.id = id;
        }
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
            this.setId(this.getValueFromDatabase(data, data.findColumn("id"), int.class));
        } catch (SQLException e) {
            System.err.println("[ERROR] Invalid ResultSet provided to model " + this.getClass().getSimpleName() +
                    ". " + e.getMessage() +
                    " Aborting database importation.");
        } catch (Exception e) {
            System.err.println("[ERROR] An unexpected error occurred");
            e.printStackTrace();
        }
    }

    private <type> type getValueFromDatabase(ResultSet data, int idx, Class clazz) {
        try {
            Method get = data.getClass().getMethod("get" + StringUtils.capitalize(clazz.getSimpleName()), int.class);
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

    private boolean persist() {
        System.out.println("[INFO] Persisting instance of " + this.getClass().getSimpleName());

        final String query = "INSERT INTO " + this.table + " " + this.generateSqlValueSet() + ";";
        try {
            Connection conn = DatabaseProvider.getDatabase();
            PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            this.fillStatement(statement);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Failed to persist new instance of " + this.getClass().getSimpleName());
            }
            this.updateId(statement);
        } catch (SQLException e) {
            System.err.println("[ERROR] An error occurred while persisting data: " + e.getMessage());
        }
        return false;
    }

    private String generateSqlValueSet() {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for(Map.Entry<String, Pair<Field, Class>> entry : this.attributes.entrySet()) {
            columns.append(", ").append(entry.getKey());
            values.append(", ?");
        }
        return "(" + columns.substring(2) + ") VALUES (" + values.substring(2) + ")";
    }

    private void fillStatement(PreparedStatement stmt) {
        int idx = 1;
        for (Map.Entry<String, Pair<Field, Class>> entry : this.attributes.entrySet()) {
            try {
                Method set = stmt.getClass().getMethod("set" + StringUtils.capitalize(entry.getValue().right.getSimpleName()), int.class, entry.getValue().right);
                set.invoke(stmt, idx++, entry.getValue().left.get(this));
            } catch (NoSuchMethodException e) {
                System.err.println("[ERROR] Unable to find method to set query statement parameter. " + e.getMessage());
            } catch (Exception e) {
                System.err.println("[ERROR] An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateId(PreparedStatement stmt) throws SQLException {
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            this.id = generatedKeys.getInt(1);
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
    }

    private boolean update() {
        System.out.println("[INFO] Updating instance of " + this.getClass().getSimpleName() + " (" + this.id + ")");

        final String query = this.generateSQLUpdate();

        try {
            Connection conn = DatabaseProvider.getDatabase();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.closeOnCompletion();
            this.assignUpdateDatas(stmt);
            return stmt.executeUpdate() != 0;
        } catch (Exception e) {
            System.err.println("[ERROR] An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    private String generateSQLUpdate() {
        final String queryBase = "UPDATE " + this.table + " SET";
        StringBuilder values = new StringBuilder();
        final String queryWhere = "WHERE " + this.table + ".id = ?";

        for (Map.Entry<String, Pair<Field, Class>> entry : this.attributes.entrySet()) {
            values.append(", ").append(this.table).append(".").append(entry.getKey()).append(" = ?");
        }
        return queryBase + " " + values.substring(2) + " " + queryWhere + ";";
    }

    private void assignUpdateDatas(PreparedStatement stmt) {
        try {
            int idx = 1;
            for (Map.Entry<String, Pair<Field, Class>> entry : this.attributes.entrySet()) {
                Method set = stmt.getClass().getMethod("set" + StringUtils.capitalize(entry.getValue().right.getSimpleName()),
                        int.class, entry.getValue().right);
                set.invoke(stmt, idx++, entry.getValue().left.get(this));
            }
            stmt.setInt(idx, this.id);
        } catch (NoSuchMethodException e) {
            System.err.println("[ERROR] Unable to find method to set query statement parameter. " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[ERROR] An error occurred while preparing updae statement: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}