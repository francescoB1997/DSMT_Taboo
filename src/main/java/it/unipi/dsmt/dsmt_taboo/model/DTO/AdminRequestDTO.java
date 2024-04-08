package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class AdminRequestDTO
{

    private String username;
    private String password;
    private String parameter;

    public AdminRequestDTO() {}
    public AdminRequestDTO(String username, String password, String parameter) {
        this.username = username;
        this.password = password;
        this.parameter = parameter;
    }

    public String getPassword() { return this.password; }

    public void setPassword(String password) { this.password = password; }

    public String getParameter() { return this.parameter; }

    public void setParameter(String parameter) { this.parameter = parameter; }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
