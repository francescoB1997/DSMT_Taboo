package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class AdminRequestDTO
{
    /**
     * This class represents a Data Transfer Object (DTO) used for carrying administrative requests,
     * particularly for handling administrative actions in the system. It contains fields to store
     * the username of the administrator initiating the request and the parameter associated with the request.
     * The class provides constructors to initialize its fields and getter and setter methods for accessing
     * and modifying the username and parameter values.
     */

    private String username;
    private String parameter;

    public AdminRequestDTO() {}
    public AdminRequestDTO(String username, String parameter) {
        this.username = username;
        this.parameter = parameter;
    }

    public String getParameter() { return this.parameter; }

    public void setParameter(String parameter) { this.parameter = parameter; }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
