package it.unipi.dsmt.dsmt_taboo.model.entity;

import it.unipi.dsmt.dsmt_taboo.model.DTO.InviteFriendRequestDTO;

import java.util.ArrayList;

public class InviteFriends {

    String gameId;
    String userInviter;
    private ArrayList<String> yourTeam;
    private ArrayList<String> roles;
    private ArrayList<String> rivals;


    public InviteFriends(InviteFriendRequestDTO inviteFriend)
    {
        this.gameId = inviteFriend.getGameId();
        this.userInviter = inviteFriend.getUserInviter();
        this.yourTeam = inviteFriend.getYourTeam();
        this.roles = inviteFriend.getRoles();
        this.rivals = inviteFriend.getRivals();
    }

    public String getGameId() { return this.gameId; }

    public void setGameId(String gameId) {
        this.gameId = gameId;
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

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public ArrayList<String> getRivals() {return this.rivals; }

    public void setRivals(ArrayList<String> rivals) { this.rivals = rivals; }
}
