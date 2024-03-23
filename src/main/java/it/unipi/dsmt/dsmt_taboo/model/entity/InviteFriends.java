package it.unipi.dsmt.dsmt_taboo.model.entity;

import it.unipi.dsmt.dsmt_taboo.model.DTO.InviteFriendRequestDTO;

import java.util.ArrayList;

public class InviteFriends {

    String gameId;
    String userInviter;
    private ArrayList<String> yourTeam; // This is the inviter team
    private ArrayList<String> roles;    // This represents the inviter team roles
    private ArrayList<String> rivals;   // This is the rivals team
    private ArrayList<String> rivalsRoles;

    public InviteFriends(InviteFriendRequestDTO inviteFriend)
    {
        this.gameId = inviteFriend.getGameId();
        this.userInviter = inviteFriend.getUserInviter();
        this.yourTeam = inviteFriend.getYourTeam();
        this.roles = inviteFriend.getRoles();
        this.rivals = inviteFriend.getRivals();
        this.rivalsRoles = inviteFriend.getRivalsRoles();
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

    public ArrayList<String> getRivalsRoles() {
        return this.rivalsRoles;
    }

    public void setRivalsRoles(ArrayList<String> rivalsRoles) {
        this.rivalsRoles = rivalsRoles;
    }
}
