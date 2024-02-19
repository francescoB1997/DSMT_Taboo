package it.unipi.dsmt.dsmt_taboo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                    this.addFriend(new FriendDTO(friendUsername, this.session.isUserLogged(friendUsername)));
                }
            }
        } catch (SQLException ex)
        {
            System.out.println("getFriendListFromDB eccezione query: " + ex.getMessage());
            return;
        }
    }
}