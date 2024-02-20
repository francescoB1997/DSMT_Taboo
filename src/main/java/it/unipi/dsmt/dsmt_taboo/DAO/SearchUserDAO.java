package it.unipi.dsmt.dsmt_taboo.DAO;

import it.unipi.dsmt.dsmt_taboo.model.DTO.FriendDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.SearchedUserDTO;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchUserDAO extends BaseFunctionalitiesDB
{
    private String username;
    private List<SearchedUserDTO> userList;
    private SessionManagement session;
    public SearchUserDAO()
    {
        this.userList = new ArrayList<>();
        this.session = SessionManagement.getInstance();
    }

    public List<SearchedUserDTO> getUserList(String userToSearch)
        // The method return the searched user or a list id the search is a generic one
    {
        this.searchUserInDB(userToSearch);
        this.userList.forEach(searchedUserDTO -> System.out.println("[" + searchedUserDTO.getUsername() + "]"));    // DEBUG
        return this.userList;
    }

    private void searchUserInDB(String userToSearch)
        // This function ask (to DB) all the users that match with 'userToSearch'
    {
        String searchUserQuery = "SELECT U.Username, U.Name, U.Surname FROM " + DB_NAME + ".user as U " +
                "WHERE U.Username LIKE ? ;";
        try (
                Connection connection = getConnection();
                PreparedStatement checkStatement = connection.prepareStatement(searchUserQuery))
        {
            checkStatement.setString(1, "%" + userToSearch + "%");
            try (ResultSet resultSet = checkStatement.executeQuery())
            {
                while (resultSet.next())
                {
                    String username = resultSet.getString("Username");
                    String name = resultSet.getString("Name");
                    String surname = resultSet.getString("Surname");
                    boolean isLogged = this.session.isUserLogged(username);
                    this.userList.add(new SearchedUserDTO(username, name, surname, isLogged));
                }
            }
        } catch (SQLException ex)
        {
            System.out.println("searchUserInDB eccezione query: " + ex.getMessage());
            return;
        }
    }


    public String getUsername() { return this.username; }

    public void setUsername(String username) { this.username = username; }
}
