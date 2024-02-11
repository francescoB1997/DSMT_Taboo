package it.unipi.dsmt.dsmt_taboo.DAO;

import it.unipi.dsmt.dsmt_taboo.exceptions.UserNotExistsException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends BaseFunctionalitiesDB
    // This class handle the UserDTO model and interact with the DB
{
    public UserDAO()
    {
        super();
    }

    public int signup(UserDTO user) {
        String checkExistingUserSQL = "SELECT * FROM intesa_vincente.user WHERE Username = ?";
        String registerUserSQL = "INSERT INTO intesa_vincente.user" +
                "(Username, Name, Surname, Password)" +
                "VALUES" +
                "(?,?,?,?);";

        try (Connection connection = getConnection())
        {
            connection.setAutoCommit(false);

            // Check if the username already exists
            try (PreparedStatement checkStatement = connection.prepareStatement(checkExistingUserSQL)) {
                checkStatement.setString(1, user.getUsername());

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Username already exists, return an error
                        return 0;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return -1;
            }


            // Insert the new user if the username doesn't exist
            try (PreparedStatement preparedStatement = connection.prepareStatement(registerUserSQL)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getFirstName());
                preparedStatement.setString(3, user.getLastName());
                preparedStatement.setString(4, user.getPassword());


                if (preparedStatement.executeUpdate() == 0) {
                    connection.rollback();
                    return -1;
                }

                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                ex.printStackTrace();
                return -1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }

        return 1;
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
                    if(userExists == 0) // then, the user can't correctly login
                        throw new UserNotExistsException();
                }
            }
        }
        catch (Exception e)
        {
            if(e.getClass() == UserNotExistsException.class)
                throw new UserNotExistsException();
            else
                System.out.println("UserDAO login Exception: " + e.getMessage());
        }
        System.out.println("Non devo essere mai qui");
    }
}
