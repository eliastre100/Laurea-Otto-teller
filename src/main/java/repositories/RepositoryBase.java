package repositories;

import annotations.Model;
import annotations.Repository;
import models.ModelBase;
import utils.DatabaseProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryBase {

    private String table = "";
    private Repository repository = null;

    RepositoryBase() {
        this.initTable();
    }

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

    private <model> void fillResult(ResultSet res, ArrayList<model> data) throws NoSuchMethodException, SQLException, IllegalAccessException, InvocationTargetException, InstantiationException {
        while (res.next()) {
            model entity = (model) this.repository.model().newInstance();
            Method method = entity.getClass().getMethod("importDatabaseData", ResultSet.class);
            method.invoke(entity, res);
            data.add(entity);
        }
    }
}
