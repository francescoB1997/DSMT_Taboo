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
    private List<FriendDTO> friendList;
    private SessionManagement session;

    public FriendDAO(String username)
    {
        System.out.println("FriendDAO: " + username);
        this.username = username;
        this.friendList = new ArrayList<>();
        this.session = SessionManagement.getInstance();
    }

    public void addFriend(FriendDTO friend) { this.friendList.add(friend); }

    public List<FriendDTO> getFriendList()
    {
        this.getFriendListFromDB();
        return this.friendList;
    }

    public void setFriendList(List<FriendDTO> friendList) { this.friendList = friendList; }

    private void getFriendListFromDB()
    {
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
                while (resultSet.next())
                {
                    String friendUsername = resultSet.getString("FriendUsername");
                    this.addFriend(new FriendDTO(friendUsername, SessionManagement.getInstance().isUserLogged(friendUsername)));
                }
            }
        } catch (SQLException ex)
        {
            System.out.println("getFriendListFromDB eccezione query: " + ex.getMessage());
            return;
        }
    }

    public boolean removeFriend(String username1, String username2)
    {
        String removeQuery = "DELETE FROM " + DB_NAME + ".friendship " +
                "WHERE (Username1 = ? AND Username2 = ?) OR (Username1 = ? AND Username2 = ?)";
                //Indipendentemente dall'ordine dei nomi degli utenti

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(removeQuery)) {
            preparedStatement.setString(1, username1);
            preparedStatement.setString(2, username2);
            preparedStatement.setString(3, username2);
            preparedStatement.setString(4, username1);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Integer addFriend(String usernameToAdd)
    {
        String addFriendQuery = "INSERT INTO " + DB_NAME + ".friendship (Username1, Username2) VALUES (?, ?)";

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

}
