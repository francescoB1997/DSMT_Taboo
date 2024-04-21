package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class UserDTO
{
    /**
     * This class represents a Data Transfer Object (DTO) used for encapsulating user data in the system.
     * It includes fields for the user's username, name, surname, password, and login status.
     * Constructors are provided to initialize the object with different combinations of these fields.
     * Getter and setter methods are available for each field to retrieve and update their values.
     */

    private String username;
    private String name;
    private String surname;
    private String password;
    private Boolean isLogged;

    public UserDTO(){};

    public UserDTO(String username, String name, String surname, String password)
    {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.isLogged = null;
    }

    public UserDTO(String username, String name, String surname, String password, Boolean isLogged)
    {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.isLogged = isLogged;
    }

    public UserDTO(String username, String name, String surname, Boolean isLogged)
    {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = null;
        this.isLogged = isLogged;
    }

    public UserDTO(String username, String name, String surname)
    {
        // This constructor is used by SearchUser functionality
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = "";
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() { return this.username; }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getLogged() {
        return isLogged;
    }

    public void setLogged(Boolean logged) {
        isLogged = logged;
    }
}
