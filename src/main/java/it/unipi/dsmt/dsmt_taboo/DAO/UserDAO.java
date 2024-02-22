package it.unipi.dsmt.dsmt_taboo.DAO;

import it.unipi.dsmt.dsmt_taboo.exceptions.UserNotExistsException;
import it.unipi.dsmt.dsmt_taboo.model.DTO.FriendDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends BaseDAO
    // This class handle the UserDTO model and interact with the DB
{
    public UserDAO() { super(); }

    public int signup(UserDTO user) {

        String checkIfUserExistsQuery = "SELECT COUNT(*) as AccountExists FROM " + DB_NAME + ".user as u WHERE (u.username = ?);";
        String signupUserQuery = "INSERT INTO " + DB_NAME + ".user VALUES(?,?,?,?);";
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            // Check if the username already exists
            try (PreparedStatement checkStatement = connection.prepareStatement(checkIfUserExistsQuery)) {
                checkStatement.setString(1, user.getUsername());
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int userExists = resultSet.getInt("AccountExists");
                        if (userExists == 1)    // Username already used
                            return 0;
                    }
                }
            } catch (SQLException ex) {
                //ex.printStackTrace();
                return -1;
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(signupUserQuery)) // We can insert the new username
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
            } catch (SQLException ex) {
                connection.rollback();
                System.out.println("Eccezione query");
                //ex.printStackTrace();
                return -1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
        return 1;
    }

    public void login(String username, String password) throws UserNotExistsException {

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
                    if (userExists == 0) // then, the user can't correctly login
                        throw new UserNotExistsException();
                }
            }
        } catch (Exception e) {
            if (e.getClass() == UserNotExistsException.class)
                throw new UserNotExistsException();
            else
                System.out.println("UserDAO login Exception: " + e.getMessage());
        }
    }

    public boolean removeUser(String username) {
        String removeQuery = "DELETE FROM " + DB_NAME + ".user" + " WHERE username = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(removeQuery))
        {
            preparedStatement.setString(1, username);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<UserDTO> globalSearchUser(String userToSearch)
    {
        List<UserDTO> globalSearchUserList = new ArrayList<>();

        String searchUserQuery = "SELECT U.Username, U.Name, U.Surname FROM " + DB_NAME + ".user as U " +
                "WHERE U.Username LIKE ? ;";

        try (
                Connection connection = getConnection();
                PreparedStatement checkStatement = connection.prepareStatement(searchUserQuery))
        {
            checkStatement.setString(1, "%" + userToSearch + "%");
            try(ResultSet resultSet = checkStatement.executeQuery())
            {
                while (resultSet.next())
                {
                    String username = resultSet.getString("Username");
                    String name = resultSet.getString("Name");
                    String surname = resultSet.getString("Surname");
                    globalSearchUserList.add(new UserDTO(username, name, surname));
                }
            }
        } catch (SQLException ex)
        {
            System.out.println("searchUserInDB eccezione query:" + ex.getMessage());
            return  null;
        }

        System.out.println("\t*** Below the matching of users based on the searchRequest: ***");
        globalSearchUserList.forEach(searchedUserDTO -> System.out.println("[" + searchedUserDTO.getUsername() + "]"));

        return  globalSearchUserList;
    }

}