package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class FriendDTO
{
    /**
     * This class represents a Data Transfer Object (DTO) used for encapsulating information about
     * a friend in the system. It contains fields to store the username of the friend and a boolean flag
     * indicating whether the friend is currently logged in. The class provides a constructor to initialize
     * its fields, as well as getter and setter methods for accessing and modifying the username and login status
     * of the friend.
     */

    private String username;
    private boolean isLogged;

    public FriendDTO(String username, boolean isLogged)
    {
        this.username = username;
        this.isLogged = isLogged;
    }

    public boolean isLogged() {
        return this.isLogged;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
