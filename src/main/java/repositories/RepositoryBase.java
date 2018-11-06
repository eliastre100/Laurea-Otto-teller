package repositories;

import anotations.Model;
import anotations.Repository;
import models.ModelBase;
import utils.DatabaseProvider;

import java.lang.annotation.Annotation;
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
            System.out.println(this.repository.model().getDeclaredAnnotations().length);
            model entity = (model) this.repository.model().newInstance();
            Method method = entity.getClass().getMethod("importDatabaseData", ResultSet.class);
            method.invoke(entity, res);
            data.add(entity);
        }
    }
}
