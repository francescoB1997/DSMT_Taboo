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

    public FriendDAO(String username)
    {
        System.out.println("FriendDAO: " + username);
        this.username = username;
    }

    public List<FriendDTO> getFriendList()
    {
        List<FriendDTO> friendList = new ArrayList<>();

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
                    friendList.add(new FriendDTO(friendUsername, SessionManagement.getInstance().isUserLogged(friendUsername)));
                }
            }
        } catch (SQLException ex)
        {
            System.out.println("searchUserInDB eccezione query:" + ex.getMessage());
            return  null;
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

}
