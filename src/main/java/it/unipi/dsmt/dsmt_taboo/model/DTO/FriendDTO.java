package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class FriendDTO
    // This class model a friend (logged or not) of the user -> username
{
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