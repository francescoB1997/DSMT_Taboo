package it.unipi.dsmt.dsmt_taboo.DAO;

import it.unipi.dsmt.dsmt_taboo.exceptions.DatabaseNotReachableException;
import it.unipi.dsmt.dsmt_taboo.exceptions.UserNotExistsException;
import it.unipi.dsmt.dsmt_taboo.model.DTO.UserDTO;
import it.unipi.dsmt.dsmt_taboo.utility.Constant;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends BaseDAO
{
    /**
     * This class implements operations to handle user entities in the MySQL database.
     * It provides methods for user signup, login, removal, and global user search.
     * The class extends the BaseDAO class for database connectivity and handles exceptions
     * related to database connectivity, such as DatabaseNotReachableException and SQLException.
     * It includes methods to insert user information into the database during signup, authenticate
     * users during login, remove users from the database, and search for users globally based on
     * their usernames. The class encapsulates the logic for interacting with the user table in the
     * database and ensures proper data handling and error logging for debugging purposes.
     */

    public UserDAO() { super(); }

    /*
     * Inserte the user info of Sign up into db
     */
    public int signup(UserDTO user)
    {
        String checkIfUserExistsQuery = "SELECT COUNT(*) as AccountExists FROM " + DB_NAME + ".user as u WHERE (u.username = ?);";
        String signupUserQuery = "INSERT INTO " + DB_NAME + ".user VALUES(?,?,?,?);";
        try (Connection connection = getConnection())
        {
            connection.setAutoCommit(false);
            // Check if the username already exists
            try (PreparedStatement checkStatement = connection.prepareStatement(checkIfUserExistsQuery)) {
                checkStatement.setString(1, user.getUsername());
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next())
                    {
                        int userExists = resultSet.getInt("AccountExists");
                        if (userExists == 1)    // Username already used
                            return 0;
                    }
                }
            } catch (SQLException ex) {
                return -1;
            }

            // We can insert the new username
            try (PreparedStatement preparedStatement = connection.prepareStatement(signupUserQuery))
            {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setString(3, user.getSurname());
                preparedStatement.setString(4, user.getPassword());

                if (preparedStatement.executeUpdate() == 0) {
                    connection.rollback();
                    return -1;
                }
                connection.commit();
            }
            catch (SQLException ex)
            {
                connection.rollback();
                System.out.println("Signup: Query EX during insert: " + ex.getMessage());
                return -1;
            }
        }
        catch (Exception ex)
        {
            if(ex instanceof DatabaseNotReachableException)
                System.out.println("Signup: DatabaseNotReachableException");
            else
                System.out.println("Signup Ex: " + ex.getMessage());
            return -1;
        }
        return 1;
    }

    /*
     * Login a user
     */
    public void login(String username, String password) throws UserNotExistsException, DatabaseNotReachableException, SQLException
    {
        String loginQuery = "SELECT COUNT(*) as AccountExists FROM " + DB_NAME + ".user as u WHERE (u.username = ? AND u.password = ?);";
        try (
                Connection connection = this.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(loginQuery)
        ) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int userExists = resultSet.getInt("AccountExists");
                    if (userExists == 0) // then, the user can't correctly log in
                        throw new UserNotExistsException();
                }
            }
        }
        catch (Exception e)
        {
            if (e instanceof UserNotExistsException)
                throw new UserNotExistsException();
            else if(e instanceof DatabaseNotReachableException)
                throw new DatabaseNotReachableException();
            else
                throw new SQLException();
        }
    }

    /*
     * Remove a user
     */
    public Boolean removeUser(String username)
    {
        String removeQuery = "DELETE FROM " + DB_NAME + ".user" + " WHERE username = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(removeQuery))
        {
            preparedStatement.setString(1, username);
            return preparedStatement.executeUpdate() > 0;

        }
        catch (Exception ex)
        {
            if(ex instanceof DatabaseNotReachableException)
                System.out.println("removeUser: DatabaseNotReachableException");
            else
                System.out.println("removeUser Ex: " + ex.getMessage());
            return false;
        }
    }

    /*
     * Search users globally
     */
    public List<UserDTO> globalSearchUser(String userToSearch)
    {
        List<UserDTO> globalSearchUserList = null;

        String searchUserQuery = "SELECT U.Username, U.Name, U.Surname FROM " + DB_NAME + ".user as U " +
                "WHERE U.Username LIKE ? ;";

        try (
                Connection connection = getConnection();
                PreparedStatement checkStatement = connection.prepareStatement(searchUserQuery))
        {
            checkStatement.setString(1, "%" + userToSearch + "%");
            try(ResultSet resultSet = checkStatement.executeQuery())
            {
                globalSearchUserList = new ArrayList<>();
                while (resultSet.next())
                {
                    String username = resultSet.getString("Username");
                    if(username.equals(Constant.usernameAdmin) || username.contains(Constant.usernameAdmin))
                        continue;
                    String name = resultSet.getString("Name");
                    String surname = resultSet.getString("Surname");

                    globalSearchUserList.add(new UserDTO(username, name, surname, SessionManagement.getInstance().isUserLogged(username)));
                }
            }
        }
        catch (Exception ex)
        {
            if(ex instanceof DatabaseNotReachableException)
                System.out.println("globalSearchUser: DatabaseNotReachableException");
            else
                System.out.println("globalSearchUser Ex: " + ex.getMessage());
            return  null;
        }

        return  globalSearchUserList;
    }
}
