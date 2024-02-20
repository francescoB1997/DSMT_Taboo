package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class SearchedUserDTO
{
    private String username;
    private String name;
    private String surname;
    private boolean isLogged;
    public SearchedUserDTO(String username, String name, String surname, boolean isLogged)
    {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.isLogged = isLogged;
    }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isLogged() { return isLogged; }
    public void setLogged(boolean logged) { isLogged = logged; }

}
