package it.unipi.dsmt.dsmt_taboo.model.entity;

import it.unipi.dsmt.dsmt_taboo.model.DTO.InviteInTeamRequestDTO;
import it.unipi.dsmt.dsmt_taboo.utility.Constant;

import java.util.ArrayList;

public class InviteInTeam {

    String id;
    String userInvite;
    private ArrayList<String> yourTeam = new ArrayList<>();
    private ArrayList<Constant> roles = new ArrayList<>();


    public InviteInTeam(InviteInTeamRequestDTO inviteFriend)
    {
        this.id = inviteFriend.getGameId();
        this.userInvite = inviteFriend.getUserInvite();
        this.yourTeam = inviteFriend.getYourTeam();
        this.roles = inviteFriend.getRoles();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserInvite() {
        return userInvite;
    }

    public void setUserInvite(String userInvite) {
        this.userInvite = userInvite;
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
