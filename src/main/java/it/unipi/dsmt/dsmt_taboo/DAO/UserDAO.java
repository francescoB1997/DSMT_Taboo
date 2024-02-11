package it.unipi.dsmt.dsmt_taboo.DAO;

import it.unipi.dsmt.dsmt_taboo.model.DTO.UserDTO;
import it.unipi.dsmt.dsmt_taboo.utility.BaseFunctionalitiesDB;
import it.unipi.dsmt.dsmt_taboo.exceptions.UserNotExistsException;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class UserDAO extends BaseFunctionalitiesDB
    // This class handle the UserDTO model and interact with the DB
{
    public UserDAO()
    {
        super();
    }

    public void login(String username, String password) throws UserNotExistsException
    {
        System.out.println("UserDAO: check username=" + username + " password=" + password);
        String loginQuery = "SELECT COUNT(*) as AccountExists FROM taboo.user as u WHERE (u.username = ? AND u.password = ?)";
        try (
                Connection connection = this.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(loginQuery)
            )
        {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    int userExists = resultSet.getInt("AccountExists");
                    System.out.println("Risultato query: " + userExists);
                    if(userExists == 0) // OK, the user is correctly logged
                        throw new UserNotExistsException();
                }
            }
        }
        catch (Exception e)
        {
            if(e.getClass() == UserNotExistsException.class)
                throw new UserNotExistsException();
            else
                System.out.println("UserDAO login: " + e.getMessage());
        }
        System.out.println("Non devo essere mai qui");
    }
}
