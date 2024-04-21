package it.unipi.dsmt.dsmt_taboo.model.entity;

import it.unipi.dsmt.dsmt_taboo.model.DTO.InviteFriendRequestDTO;

import java.util.ArrayList;

public class InviteFriends
{
    /**
     * This class represents an invitation to a game session. It encapsulates information about the game,
     * the inviting user, the teams involved, and the roles assigned to each team member. The class initializes
     * its fields using data from an InviteFriendRequestDTO object and provides methods to retrieve and set
     * information about the game ID, inviting user, teams, and roles. Additionally, it includes a method
     * to print detailed information about the invitation, including the requester, game ID, team members,
     * and their respective roles. The class facilitates the management and representation of game invitations
     * within the application.
     */

    String gameId;
    String userInviter;
    private ArrayList<String> yourTeam; // This is the inviter team
    private ArrayList<String> roles;    // This represents the inviter team roles
    private ArrayList<String> rivals;   // This is the rivals team
    private ArrayList<String> rivalsRoles; // This represents the rivals team roles

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

    public void printInfoInvite()
    {
        System.out.println("Invitation: Requester[" + this.userInviter + "] , ID_Request[" + this.gameId + "]");
        System.out.print("Team Blue { ");
        this.yourTeam.forEach(friendUsername -> System.out.print("[" + friendUsername + "] "));
        System.out.println(" }");
        System.out.print("Roles Team Blue { ");
        this.roles.forEach(inviterRole -> System.out.print("[" + inviterRole + "]"));
        System.out.println(" }");
        System.out.print("Team Red { ");
        this.rivals.forEach(rivalUsername -> System.out.print("[" + rivalUsername + "] "));
        System.out.println(" }");
        System.out.print("Roles Team Red { ");
        this.rivalsRoles.forEach(rivalRole -> System.out.print("[" + rivalRole + "]"));
        System.out.println(" }");
    }

}
