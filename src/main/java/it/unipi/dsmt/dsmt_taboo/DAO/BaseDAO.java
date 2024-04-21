package it.unipi.dsmt.dsmt_taboo.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;
import it.unipi.dsmt.dsmt_taboo.exceptions.DatabaseNotReachableException;


public class BaseDAO
{
    /**
     * This class provides basic Data Access Object (DAO) functionality for connecting to
     * and communicating with the MySQL database. It includes methods to set up a data source
     * and retrieve a connection to the database. The class initializes the data source upon
     * instantiation and provides a getConnection() method to obtain a database connection.
     * It handles exceptions related to database connectivity, such as DatabaseNotReachableException,
     * by throwing an appropriate exception when the connection cannot be established.
     * The class also defines constants for database configuration parameters such as database name,
     * IP address, URL, username, and password.
     */

    public static final String DB_NAME = "taboo";
    private static final String DB_IP = "10.2.1.131";
    private static final String DB_URL = "jdbc:mysql://" + DB_IP + ":3306/" + DB_NAME;
    private static final String DB_USERNAME = "db_user";
    private static final String DB_PASSWORD = "dbu";
    private DataSource dataSource = null;

    public BaseDAO() {
        dataSource = this.setupDataSource();
        if (dataSource == null)
            System.out.println("BaseFunctionalitiesDB: Connection to DB FAILED!");
    }

    private DataSource setupDataSource()
    {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(DB_URL);
        dataSource.setUser(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        return dataSource;
    }

    public Connection getConnection() throws DatabaseNotReachableException
    {
        try {
            return dataSource.getConnection();
        }
        catch (SQLException e)
        {
            System.out.println("BaseFunctionalitiesDB: Can't get connection");
            if(e.getSQLState().equals("08S01"))
                throw new DatabaseNotReachableException();
            return null;
        }
    }
}