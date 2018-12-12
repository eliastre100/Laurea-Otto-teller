package utils;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Provide the database access. It follow the singleton pattern
 * @author Antoine FORET
 * @version 1.0
 */
public class DatabaseProvider {
    /* Parameters for the database connection. The database must be a MySQL database */
    private static final String host = "localhost:32769";
    private static final String database = "otto-teller";
    private static final String username = "root";
    private static final String password = "root";

    /**
     * Database connection instance
     */
    static private Connection conn = null;

    /**
     * Retrieve the database connection either by creating it or using the previously created.
     * @return a database connection
     */
    public static Connection getDatabase() {
        if (conn == null) {
            initConnection();
        }
        return conn;
    }

    /**
     * Initialize a new connection to the database and store it within the class static variable
     */
    private static void initConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
        } catch (Exception e) {
            System.err.println("[ERROR] Unable to connect to the database (host: " + host + ", database: " + database + ")" );
            e.printStackTrace();
        }
    }
}
