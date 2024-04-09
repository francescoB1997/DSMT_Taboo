package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class AdminRequestDTO
{

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
