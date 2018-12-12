package models;

import annotations.Attribute;
import annotations.Model;
import annotations.OneToOne;
import repositories.RepositoryBase;
import utils.DatabaseProvider;
import utils.Pair;
import utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Base for each model. Every class that inherit from it is considered as a model and as such have certain persistence properties. They also need to have the corresponding annotations (Model, Attribute, OneToOne)
 * @author Antoine FORET
 * @version 1.0
 */
public class ModelBase {

    /**
     * The common attribute for each model. Each model must have an ID as such it is present here
     */
    @Attribute(name="id")
    private int id = -1;

    /**
     * List of all the attributes of the model
     */
    private HashMap<String, Pair<Field, Class>> attributes = new HashMap<>();

    /**
     * List of all the relations of the model
     */
    private HashMap<String, Pair<Field, Class>> relations = new HashMap<>();

    /**
     * The table to persist the different entities
     */
    private String table = "";

    /**
     * Constructor of any model. It initialize all the refection discoveries and the model attributes
     */
    ModelBase() {
        System.out.println("[INFO] Initializing model " + this.getClass().toString());
        this.initTable();
        this.initAttributes();
    }

    /**
     * Convert any model into a string. This is a base method that is useful for debugging but can overwrite by any model if needed.
     * @return a string representation of the model including all its attributes and relations
     */
    public String toString() {
        try {
            StringBuilder res = new StringBuilder("[Model " + this.getClass().getSimpleName() + "(" + this.id + "): {");
            for (Map.Entry<String, Pair<Field, Class>> entry : this.attributes.entrySet()) {
                res.append(entry.getValue().left.getName()).append(": ").append(entry.getValue().left.get(this)).append(", ");
            }
            for (Map.Entry<String, Pair<Field, Class>> entry : this.relations.entrySet()) {
                res.append(entry.getValue().left.getName()).append(": ").append(entry.getValue().left.get(this)).append(", ");
            }
            return res.substring(0, res.length() - 2) + "}]";
        } catch (IllegalAccessException e) {
            return "[Model " + this.getClass().getSimpleName() + "(" + this.id + "): {FAILED TO RETRIEVE FIELDS}]";
        }
    }

    /**
     * Public interface to import the entity value inside the instance.
     * @param res the database result correctly initialized (.next must already have been called)
     */
    public void importDatabaseData(ResultSet res) {
        this.fromDatabase(res);
    }

    /**
     * Public interface to save the state of an entity. It will depending on the current persistence status either save the entity or update it. This choice is based on the id as only a persisted instance will have one
     * @return if the save is successful
     */
    public boolean save() {
        this.persistRelations();
        if (this.id == -1) {
            return this.persist();
        } else {
            return this.update();
        }
    }

    /**
     * Public interface to delete an entity from the database
     * @return if the deletion was successful
     */
    public boolean destroy() {
        if (this.id != -1) {
            return this.delete();
        }
        return false;
    }

    /**
     * Id getter
     * @return the current instance id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Make sure that every relation is persisted before the current instance is saved cause else a relation might not be persisted at all and the datas would be corrupted
     */
    private void persistRelations() {
        for (Map.Entry<String, Pair<Field, Class>> entry : this.relations.entrySet()) {
            try {
                ModelBase model = (ModelBase) entry.getValue().left.get(this);
                if (model != null) {
                    model.save();
                }
            } catch (IllegalAccessException e) {
                System.err.println("[ERROR] Unable to access relation item " + entry.getValue().left.getName() + " on " + this.getClass().getSimpleName());
            }
        }
    }

    /**
     * Setter for the instance id. As we base our persistence and all database access within it, it cannot be modified by the user, it can only be modifier internally
     * @param id the id to define
     */
    private void setId(Integer id) {
        if (id != null) {
            this.id = id;
        }
    }

    /**
     * Initialize the table field of the instance based on the model annotation
     */
    private void initTable() {
        Model model = this.getClass().getAnnotation(Model.class);
        if (model != null) {
            this.table = model.table();
        }
    }

    /**
     * Search for all attributes of the model (Attribute annotation) and store them for future use
     */
    private void initAttributes() {
        for (Field field : this.getClass().getDeclaredFields()) {
            Attribute attribute = field.getAnnotation(Attribute.class);
            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
            if (attribute != null) {
                attributes.put(attribute.name(), new Pair<>(field, field.getType()));
                System.out.println("[INFO] Attribute " + attribute.name() + " found on " + field.getName() + " (" + field.getType().toString() + ")");
            } else if (oneToOne != null) {
                relations.put(oneToOne.name(), new Pair<>(field, field.getType()));
                System.out.println("[INFO] Relation " + oneToOne.name() + " found on " + field.getName() + " (" + field.getType().toString() + ")");
            }
        }
    }

    /**
     * Import the data from a sql request inside the instance
     * @param data the result from a database query
     */
    private void fromDatabase(ResultSet data) {
        try {
            for(Map.Entry<String, Pair<Field, Class>> entry : this.attributes.entrySet()) {
                int idx = data.findColumn(entry.getKey());
                this.setValueFromDb(entry.getValue().left, this.getValueFromDatabase(data, idx, entry.getValue().right));
            }
            for(Map.Entry<String, Pair<Field, Class>> entry : this.relations.entrySet()) {
                int idx = data.findColumn(entry.getKey());
                this.setValueFromDb(entry.getValue().left, this.getRelationInstance(data.getInt(idx), entry.getValue().left));
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

    /**
     * Retrieve an relationship. In order to accomplish this action, it base on the relation annotation parameters, including the repository one to retrieve the data from the database
     * @param id the id of the relation
     * @param field the field where the annotation is defined
     * @param <model> the class of the model to create. It must be equal to the value of the relation annotation
     * @return an instance of the relation entity
     */
    private <model> model getRelationInstance(int id, Field field) {
        try {
            Class repositoryClass = field.getAnnotation(OneToOne.class).repository();
            RepositoryBase repository = (RepositoryBase) repositoryClass.newInstance();
            return repository.find(id);
        } catch (Exception e) {
            System.err.println("[ERROR] Unable to retrieve relation");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Extract a value from the database result.
     * @param data the sql result
     * @param idx the index where the data to extract is
     * @param clazz the class we want the data to be
     * @param <type> the type that will be return, it is the same value as the clazz parameter
     * @return return the data stored in the sql result
     */
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

    /**
     * Define a field value based on the database value. It doesn't retrieve the information from the sql result but get it from a function that take care of it
     * @param field the filed to update
     * @param value the value to define
     * @param <valueType> the type of the value to define
     */
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

    /**
     * Save an entity for the first time in the database. It use complementary functions to create the sql string and fill it.
     * @return if the persist action succeed
     */
    private boolean persist() {
        System.out.println("[INFO] Persisting instance of " + this.getClass().getSimpleName());

        final String query = "INSERT INTO " + this.table + " " + this.generateSqlValueSet() + ";";
        try {
            Connection conn = DatabaseProvider.getDatabase();
            PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.closeOnCompletion();
            this.fillStatement(statement);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Failed to persist new instance of " + this.getClass().getSimpleName());
            }
            this.updateId(statement);
            return true;
        } catch (SQLException e) {
            System.err.println("[ERROR] An error occurred while persisting data: " + e.getMessage());
        }
        return false;
    }

    /**
     * Generate the sql request string for a new addition to the database
     * @return the sql request WITHOUT the parameters filled
     */
    private String generateSqlValueSet() {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for(Map.Entry<String, Pair<Field, Class>> entry : this.attributes.entrySet()) {
            columns.append(", ").append(entry.getKey());
            values.append(", ?");
        }
        for(Map.Entry<String, Pair<Field, Class>> entry : this.relations.entrySet()) {
            columns.append(", ").append(entry.getKey());
            values.append(", ?");
        }
        return "(" + columns.substring(2) + ") VALUES (" + values.substring(2) + ")";
    }

    /**
     * Fill a prepared statement with the current instance values
     * @param stmt the prepared statement to fill
     */
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
        for (Map.Entry<String, Pair<Field, Class>> entry : this.relations.entrySet()) {
            try {
                stmt.setInt(idx++, ((ModelBase) entry.getValue().left.get(this)).getId());
            } catch (SQLException e) {
                System.err.println("[ERROR] Unable to find method to set query statement parameter. " + e.getMessage());
            } catch (Exception e) {
                System.err.println("[ERROR] An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();            }
        }
    }

    /**
     * Retrieve the id of the entity from the performed persistence request in order to have the id defined in our instance.
     * @param stmt the prepared statement that was executed to insert the datas
     * @throws SQLException if the sql request didn't generated an id
     */
    private void updateId(PreparedStatement stmt) throws SQLException {
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            this.id = generatedKeys.getInt(1);
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
    }

    /**
     * Update an entity in the database. The instance must have already been persisted to work (have an id)
     * @return if the update action succeed
     */
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

    /**
     * Generate the update request WITHOUT any parameters defined.
     * @return the raw sql request without any actual data to store
     */
    private String generateSQLUpdate() {
        final String queryBase = "UPDATE " + this.table + " SET";
        StringBuilder values = new StringBuilder();
        final String queryWhere = "WHERE " + this.table + ".id = ?";

        for (Map.Entry<String, Pair<Field, Class>> entry : this.attributes.entrySet()) {
            values.append(", ").append(this.table).append(".").append(entry.getKey()).append(" = ?");
        }
        for (Map.Entry<String, Pair<Field, Class>> entry : this.relations.entrySet()) {
            values.append(", ").append(this.table).append(".").append(entry.getKey()).append(" = ?");
        }
        return queryBase + " " + values.substring(2) + " " + queryWhere + ";";
    }

    /**
     * Fill a prepared statement for an update using the actual instance values
     * @param stmt the statement to fill
     */
    private void assignUpdateDatas(PreparedStatement stmt) {
        try {
            int idx = 1;
            for (Map.Entry<String, Pair<Field, Class>> entry : this.attributes.entrySet()) {
                Method set = stmt.getClass().getMethod("set" + StringUtils.capitalize(entry.getValue().right.getSimpleName()),
                        int.class, entry.getValue().right);
                set.invoke(stmt, idx++, entry.getValue().left.get(this));
            }
            for (Map.Entry<String, Pair<Field, Class>> entry : this.relations.entrySet()) {
                stmt.setInt(idx++, ((ModelBase) entry.getValue().left.get(this)).getId());
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

    /**
     * Delete an instance form the database. This action cannot be undone (but as the instance isn't deleted we can persist it again to create a new record)
     * @return if the delete action succeed
     */
    private boolean delete() {
        System.out.println("[INFO] Removing database entry for model " + this.getClass().getSimpleName() + " with id " + this.id);
        String query = "DELETE FROM " + this.table + " WHERE " + this.table + ".id = ?;";

        try {
            Connection conn = DatabaseProvider.getDatabase();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.closeOnCompletion();
            stmt.setInt(1, this.id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Unable to delete database entry for " + this.getClass().getSimpleName() + " (" + this.id + ")");
            }
            this.id = -1;
            return true;
        } catch (SQLException e) {
            System.err.println("[ERROR] An SQL error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
}
