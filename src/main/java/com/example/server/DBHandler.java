package com.example.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
*
* The class {@code DBHandler} provides a database control object to perform database operations.
*
**/
public class DBHandler {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/wine_shop?useSSL=false";
    private static final String USERNAME = "mariomattiasulmonte";
    private static final String PASSWORD = "qezxeZ-8nevwy-kofnah";

    private Connection connection;
    /*
     * Class Constructor
     */
    public DBHandler() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("DB Initialization error");
        }
    }
    /*
     * Returns Connection data
     */
    public Connection getConnection() {
        return connection;
    }
    /*
     * Executes Select type Queries, returns ResultSet type Object
     */
    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        ResultSet resultSet = preparedStatement.executeQuery(); 
        return resultSet;
    }
    /*
     * executeUpdate(String query, Object... params) is a function that executes DB operation that
     * dont return a ResultSet but a Int instead
     */
    public int executeUpdate(String query, Object... params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeUpdate();
    }
    /*
     * Terminates DB
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // You can add more methods to interact with the database here
}



