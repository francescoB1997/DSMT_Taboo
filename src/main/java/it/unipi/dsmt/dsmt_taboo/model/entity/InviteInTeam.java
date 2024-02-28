package it.unipi.dsmt.dsmt_taboo.model.entity;

import it.unipi.dsmt.dsmt_taboo.model.DTO.InviteInTeamRequestDTO;
import it.unipi.dsmt.dsmt_taboo.utility.Constant;

import java.util.ArrayList;

public class InviteInTeam {

    String id;
    String userInviter;
    private ArrayList<String> yourTeam;
    private ArrayList<Constant> roles;


    public InviteInTeam(InviteInTeamRequestDTO inviteFriend)
    {
        this.id = inviteFriend.getGameId();
        this.userInviter = inviteFriend.getUserInviter();
        this.yourTeam = inviteFriend.getYourTeam();
        this.roles = inviteFriend.getRoles();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserInviter() {
        return userInviter;
    }

    public void setUserInviter(String userInviter) {
        this.userInviter = userInviter;
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
}
