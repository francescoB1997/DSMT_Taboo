package it.unipi.dsmt.dsmt_taboo.model.DTO;

import it.unipi.dsmt.dsmt_taboo.utility.Constant;

import java.util.ArrayList;

public class InviteInTeamRequestDTO {

    private String gameId;
    private ArrayList<String> yourTeam;
    private ArrayList<Constant> roles;
    private String userInviter;

    public InviteInTeamRequestDTO(){}

    public InviteInTeamRequestDTO(String gameId, ArrayList<String> yourTeam,
                                  ArrayList<Constant> roles, String userInviter)
    {
        this.gameId = gameId;
        this.yourTeam = yourTeam;
        this.roles = roles;
        this.userInviter = userInviter;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public ArrayList<String> getYourTeam() {
        return yourTeam;
    }

    public void setYourTeam(ArrayList<String> yourTeam) {
        this.yourTeam = yourTeam;
    }

    public ArrayList<Constant> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<Constant> roles) {
        this.roles = roles;
    }

    public String getUserInviter() {
        return userInviter;
    }

    public void setUserInviter(String userInviter)
    {
        this.userInviter = userInviter;
    }
}
