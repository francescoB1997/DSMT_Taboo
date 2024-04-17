package it.unipi.dsmt.dsmt_taboo.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import it.unipi.dsmt.dsmt_taboo.model.DTO.FriendDTO;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;

public class FriendDAO extends BaseDAO
    // This class represents the friendships of a specific user -> username
{
    private String username;

    public FriendDAO(String username)
    {
        //System.out.println("FriendDAO: " + username);
        this.username = username;
    }

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
        } catch (SQLException ex)
        {
            System.out.println("searchUserInDB eccezione query:" + ex.getMessage());
        }

        return friendList;
    }
    public boolean removeFriendDB(String usernameToRemove) {
        String removeQuery = "DELETE FROM " + DB_NAME + ".friendship " +
                "WHERE (Username1 = ? AND Username2 = ?) OR (Username1 = ? AND Username2 = ?)";
        //Indipendentemente dall'ordine dei nomi degli utenti

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(removeQuery)) {
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, usernameToRemove);
            preparedStatement.setString(3, usernameToRemove);
            preparedStatement.setString(4, this.username);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

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
        catch (SQLException ex)
        {
            System.out.println("checkIfUserExists query exception: " + ex.getMessage());
        }
        return result;
    }

    public Integer addFriend(String usernameToAdd)
    {
        String addFriendQuery = "INSERT INTO " + DB_NAME + ".friendship (Username1, Username2) VALUES (?, ?)";

        if(!checkIfUserExists(usernameToAdd))
        {
            System.out.println("Non esiste");
            return -1;
        }

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
            System.out.println("addFriend query exception: già amici");
            return 0;
        }
        catch (SQLException ex)
        {
            System.out.println("addFriend query exception: " + ex.getMessage());
            return -1;
        }
    }

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
        return alreadyFriend;
    }

}
