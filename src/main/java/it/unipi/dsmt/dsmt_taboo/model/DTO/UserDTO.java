package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class UserDTO
{
    private String name;
    private String surname;
    private String password;
    private String username;

    public UserDTO(String name, String surname, String username, String password)
    {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.username= username;
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
