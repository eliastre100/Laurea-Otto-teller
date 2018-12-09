package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseProvider {
    private static final String host = "localhost:32769";
    private static final String database = "otto-teller";
    private static final String username = "root";
    private static final String password = "root";

    static private Connection conn = null;

    public static Connection getDatabase() {
        if (conn == null) {
            initConnection();
        }
        return conn;
    }

    private static void initConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
        } catch (Exception e) {
            System.err.println("[ERROR] Unable to connect to the database (host: " + host + ", database: " + database + ")" );
            e.printStackTrace();
        }
    }
}
