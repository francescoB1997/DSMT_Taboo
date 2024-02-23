package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class UserDTO
{
    private String username;
    private String name;
    private String surname;
    private String password;

    public UserDTO() {};

    public UserDTO(String username, String name, String surname, String password)
    {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
    }

    public UserDTO(String username, String name, String surname)
        // This constructor is used by SearchUser functionality
    {
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
}
