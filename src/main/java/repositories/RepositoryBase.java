package repositories;

import annotations.Model;
import annotations.Repository;
import models.ModelBase;
import utils.DatabaseProvider;
import utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Base for every repositories. It define the way to read datas from the database. From the outside, every class that inherit from this class and use the correct annotation might seem magic but it is only due to the reflection from Java
 * @author Antoine FORET
 * @version 1.0
 */
public class RepositoryBase {

    /**
     * The table to search in. This property is defined by the model annotation during the construction of the repository object
     */
    private String table = "";

    /**
     * The repository annotation. Contain the class of the model.
     */
    private Repository repository = null;

    /**
     * Constructor of the repository. It initiate all the variables of this object.
     */
    RepositoryBase() {
        this.initTable();
    }

    /**
     * All the user to search for all entities related to this repository. It will return the whole list of entities as such, this method could end up with a lot of data and memory usage
     * @param <model> the class that we made the list of. This is a template parameter. It always need to be equal to the repository's model class
     * @return the list of all entities in database
     */
    public <model> List<model> findAll() {
        String query = "SELECT * FROM " + table;
        ArrayList<model> result = new ArrayList<>();

        try {
            Connection conn = DatabaseProvider.getDatabase();
            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(query);
            this.fillResult(res, result);
        } catch (SQLException e) {
            System.err.println("[ERROR][SQL] Sql exception");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Search for one specific entity in database.
     * @param id the id of the entity we search for.
     * @param <model> the class of the instance generated. This template variable must be equal to the repository's model class
     * @return an instance of the entity we search for or null
     */
    public <model> model find(int id) {
        ModelBase model = null;
        String query = "SELECT * FROM " + table + " WHERE " + table + ".id = ?;";

        try {
            Connection conn = DatabaseProvider.getDatabase();
            PreparedStatement statement = conn.prepareStatement(query);
            statement.closeOnCompletion();
            statement.setInt(1, id);
            ResultSet res = statement.executeQuery();
            if (!res.next()) { return null; }
            model =  (ModelBase) this.repository.model().newInstance();
            model.importDatabaseData(res);
        } catch (SQLException e) {
            System.err.println("[ERROR][SQL] Sql exception");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error");
            e.printStackTrace();
        }
        return (model) model;
    }

    /**
     * Allow to search using a specific field and retrieve all the entities that match this search value.
     * @param field the field we want to search with
     * @param value the value that must match an entity to be retried
     * @param <model> the class of the generated instances. Must be the class of the repository's model
     * @param <valueType> the type of the search value. Can be any type so this function can take any parameter as it's second parameter.
     * @return
     */
    public <model, valueType> List<model> findBy(String field, valueType value) {
        String query = "SELECT * FROM " + table + " WHERE " + field + " = ?";
        ArrayList<model> result = new ArrayList<>();

        try {
            Connection conn = DatabaseProvider.getDatabase();
            PreparedStatement statement = conn.prepareStatement(query);
            this.setFieldValue(statement, 1, value);
            ResultSet res = statement.executeQuery();
            this.fillResult(res, result);
        } catch (SQLException e) {
            System.err.println("[ERROR][SQL] Sql exception");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Initialize the repository internal variables. (Table and Repository annotation)
     */
    private void initTable() {
        try {
            this.repository = this.getClass().getAnnotation(Repository.class);
            Class modelClass = this.repository.model();
            Model model = (Model) modelClass.getAnnotation(Model.class);
            this.table = model.table();
        } catch (Exception e) {
            System.err.println("[ERROR] Badly formatted repository check that Repository AND model annotations are present");
        }
    }

    /**
     * Generate a list of instance of entities based on the result from the database.
     * @param res the response from the database
     * @param data the list we want to fill
     * @param <model> the class of the instances. Must be equal to the repository's model
     * @throws NoSuchMethodException if the class cannot be filled by the result
     * @throws SQLException if there is a database error
     * @throws IllegalAccessException if the accessibility of any method is wrong (private or protected)
     * @throws InvocationTargetException if we fail to instantiate the filling method
     * @throws InstantiationException if we fail to instantiate an entity
     */
    private <model> void fillResult(ResultSet res, ArrayList<model> data) throws NoSuchMethodException, SQLException, IllegalAccessException, InvocationTargetException, InstantiationException {
        while (res.next()) {
            model entity = (model) this.repository.model().newInstance();
            Method method = entity.getClass().getMethod("importDatabaseData", ResultSet.class);
            method.invoke(entity, res);
            data.add(entity);
        }
    }

    /**
     * Fill the prepared statement using reflection to manage all kind of parameters
     * @param stmt the statement to fill
     * @param idx the index where to fill the value
     * @param value the value to insert
     * @param <valueType> the type of the value. Can be any type
     * @throws SQLException if an sql error occur
     */
    private <valueType> void setFieldValue(PreparedStatement stmt, int idx, valueType value) throws SQLException {
        if (value instanceof ModelBase) {
            stmt.setInt(idx, ((ModelBase) value).getId());
        } else {
            try {
                Method set = stmt.getClass().getMethod("set" + StringUtils.capitalize(value.getClass().getSimpleName()), int.class, value.getClass());
                set.invoke(stmt, idx, value);
            } catch (Exception e) {
                System.err.println("[ERROR] An unexpected error occurred while filling repository request");
                e.printStackTrace();
            }
        }
    }
}
