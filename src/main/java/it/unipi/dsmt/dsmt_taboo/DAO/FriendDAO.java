package it.unipi.dsmt.dsmt_taboo.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import it.unipi.dsmt.dsmt_taboo.exceptions.DatabaseNotReachableException;
import it.unipi.dsmt.dsmt_taboo.model.DTO.FriendDTO;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;

public class FriendDAO extends BaseDAO
{
    /**
     * This class implements operations to handle friendships of a specific user in the MySQL Database.
     * It provides methods to retrieve the list of friends for a user, remove a friend from the user's friend list,
     * and add a friend for the user. The class utilizes the BaseDAO class for database connectivity.
     * It handles exceptions related to database connectivity, such as DatabaseNotReachableException,
     * and ensures error logging for debugging purposes. The class also includes private methods
     * to check if a user exists in the database and if a specified user is already a friend of the current user.
     * The class maintains the username of the current user and encapsulates it for use in the DAO operations.
     */

    private String username;

    public FriendDAO(String username)
    {
        this.username = username;
    }

    /*
     * Retrieves the list of friends for the user
     */
    public List<FriendDTO> getFriendList()
    {
        List<FriendDTO> friendList = null;

        String getAllFriendQuery = "SELECT CASE WHEN F.Username1 = ? then F.Username2 else F.Username1 END as FriendUsername FROM " +
                DB_NAME + ".friendship as F where Username1 = ? OR Username2 = ? ";

        try (
                Connection connection = getConnection();
                PreparedStatement checkStatement = connection.prepareStatement(getAllFriendQuery))
        {
            checkStatement.setString(1, this.username);
            checkStatement.setString(2, this.username);
            checkStatement.setString(3, this.username);
            try (ResultSet resultSet = checkStatement.executeQuery())
            {
                friendList = new ArrayList<>();
                while (resultSet.next())
                {
                    String friendUsername = resultSet.getString("FriendUsername");
                    friendList.add(new FriendDTO(friendUsername, SessionManagement.getInstance().isUserLogged(friendUsername)));
                }
            }
        }
        catch (Exception ex)
        {
            if(ex instanceof DatabaseNotReachableException)
                System.out.println("getFriendList: DatabaseNotReachableException");
            else
                System.out.println("getFriendList Ex: " + ex.getMessage());
        }

        return friendList;
    }

    /*
     * Removes a friend from the user's friend list
     */
    public boolean removeFriendDB(String usernameToRemove) {
        String removeQuery = "DELETE FROM " + DB_NAME + ".friendship " +
                "WHERE (Username1 = ? AND Username2 = ?) OR (Username1 = ? AND Username2 = ?)";
        //Regardless of the order of users' names

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(removeQuery)) {
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, usernameToRemove);
            preparedStatement.setString(3, usernameToRemove);
            preparedStatement.setString(4, this.username);

            System.out.println("removeFriendDB: OK");

            return preparedStatement.executeUpdate() > 0;
        }
        catch (Exception ex)
        {
            if(ex instanceof DatabaseNotReachableException)
                System.out.println("removeFriendDB: DatabaseNotReachableException");
            else
                System.out.println("removeFriendDB Ex: " + ex.getMessage());
            return false;
        }
    }

    /*
     * Checks if the specified user exists in the database
     */
    private Boolean checkIfUserExists(String username)
    {
        Boolean result = false;
        String userExistsQuery = "SELECT userExists(?) as Exists_;";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(userExistsQuery))
        {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                result = (resultSet.getInt("Exists_") == 1) ? true : false;
        }
        catch (Exception ex)
        {
            if(ex instanceof DatabaseNotReachableException)
                System.out.println("checkIfUserExists: DatabaseNotReachableException");
            else
                System.out.println("checkIfUserExists Ex: " + ex.getMessage());
        }
        return result;
    }

    /*
     * Adds a friend for the user
     */
    public Integer addFriend(String usernameToAdd)
    {
        String addFriendQuery = "INSERT INTO " + DB_NAME + ".friendship (Username1, Username2) VALUES (?, ?)";

        if(! checkIfUserExists(usernameToAdd))
            return -1;

        if(isAlreadyFriend(usernameToAdd))
            return 0;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(addFriendQuery))
        {
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, usernameToAdd);
            return (preparedStatement.executeUpdate() > 0) ? 1 : 0;
        }
        catch (SQLIntegrityConstraintViolationException ex) //Handle the Duplicate entry exception (ALREADY FRIEND)
        {
            System.out.println("addFriend query exception: Already Friends");
            return 0;
        }
        catch (Exception ex)
        {
            if(ex instanceof DatabaseNotReachableException)
                System.out.println("addFriend: DatabaseNotReachableException");
            else
                System.out.println("addFriend Ex: " + ex.getMessage());
            return -1;
        }
    }

    /*
     * Checks if the specified user is already a friend of the current user
     */
    private Boolean isAlreadyFriend(String username2)
    {
        Boolean alreadyFriend = false;
        String userExistsQuery = "SELECT alreadyFriend(?, ?) as IsAlreadyFriend;";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(userExistsQuery))
        {
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, username2);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                alreadyFriend = (resultSet.getInt("IsAlreadyFriend") > 0) ? true : false;
        }
        catch (SQLException ex)
        {
            System.out.println("checkIfUserExists query exception: " + ex.getMessage());
        }
        catch (DatabaseNotReachableException ex)
        {
            System.out.println("isAlreadyFriend: DatabaseNotReachableException");
        }
        return alreadyFriend;
    }

}
