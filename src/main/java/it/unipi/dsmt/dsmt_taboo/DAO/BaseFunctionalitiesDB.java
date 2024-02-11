package it.unipi.dsmt.dsmt_taboo.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class BaseFunctionalitiesDB
    // Base functionalities to interact with DB
{
    private static final String DB_URL = "jdbc:mysql://localhost:3306/taboo";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";
    private static Connection connection = null;

    public BaseFunctionalitiesDB()
    {
        connection = this.getConnection();
        if(connection == null)
            System.out.println("BaseFunctionalitiesDB: Connection DB FAILED!");
    }

    public void closeConnection() {
        try
        {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            System.out.println("BaseFunctionalitiesDB: Can't close connection");
            //e.printStackTrace();
        }
    }

    public Connection getConnection()
    {
        try
        {
            if (connection == null || connection.isClosed())
                connection = DriverManager.getConnection(this.DB_URL, this.DB_USERNAME, this.DB_PASSWORD);
        }
        catch (SQLException e)
        {
            //e.printStackTrace();
            connection = null;
        }
        return connection;
    }

}
