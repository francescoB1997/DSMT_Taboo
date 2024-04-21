package it.unipi.dsmt.dsmt_taboo.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequestDTO
{
    /**
     * This class represents a Data Transfer Object (DTO) used for encapsulating login request
     * information in the system. It includes fields to store the username and password provided
     * by a user attempting to log in. The class is annotated with Jackson's @JsonInclude annotation
     * to specify that fields with null values should be excluded during JSON serialization.
     * The class provides getter and setter methods for accessing and modifying the username and password attributes.
     */

    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}

