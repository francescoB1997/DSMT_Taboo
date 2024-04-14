package it.unipi.dsmt.dsmt_taboo.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.*;
import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

public class BaseDAO {
    public static final String DB_NAME = "taboo";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";
    private DataSource dataSource = null;

    public BaseDAO() {
        dataSource = this.setupDataSource();
        if (dataSource == null)
            System.out.println("BaseFunctionalitiesDB: Connection to DB FAILED!");
    }

    public void closeConnection() {
        // Non è necessario chiudere la connessione poiché il DataSource gestirà le connessioni
    }

    private DataSource setupDataSource()
    {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(DB_URL);
        dataSource.setUser(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        return dataSource;
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("BaseFunctionalitiesDB: Can't get connection");
            // e.printStackTrace();
            return null;
        }
    }
}


/*
public class BaseDAO
    // Base functionalities to interact with DB
{
    public static final String DB_NAME = "taboo";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";
    private Connection connection = null;

    public BaseDAO()
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

 */
