package it.unipi.dsmt.dsmt_taboo.model.DTO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class InviteFriendRequestDTO
{
    /**
     * This class represents a Data Transfer Object (DTO) used for encapsulating information about
     * a friend invitation request in the system. It contains fields to store details such as the game ID,
     * the user inviting friends, the invited friends, their respective roles in the game, and the roles of rivals.
     * The class provides constructors to initialize its fields, as well as getter and setter methods for accessing
     * and modifying the invitation details. Additionally, it includes a method to generate a unique game ID
     * if one is not provided and a method to print the invitation information for debugging purposes.
     */

    private String gameId;
    private ArrayList<String> yourTeam;
    private ArrayList<String> roles;
    private String userInviter;
    private ArrayList<String> rivals;
    private ArrayList<String> rivalsRoles;
    public InviteFriendRequestDTO() {}

    public InviteFriendRequestDTO(String gameId, ArrayList<String> yourTeam,
                                  ArrayList<String> roles, String userInviter,
                                  ArrayList<String> userRival, ArrayList<String> rivalsRoles)
    {
        this.gameId = gameId;
        this.yourTeam = yourTeam;
        this.roles = roles;
        this.userInviter = userInviter;
        this.rivals = userRival;
        this.rivalsRoles = rivalsRoles;
    }

    public String getGameId() { return this.gameId;}

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setAutoGameId()
    {
        if (this.gameId == "")
            this.gameId = getUniqueGameId();
    }

    public ArrayList<String> getYourTeam() {
        return this.yourTeam;
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

    public String getUserInviter() {
        return userInviter;
    }

    public void setUserInviter(String userInviter)
    {
        this.userInviter = userInviter;
    }

    private String getUniqueGameId()
    {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp.toString();
    }

    public ArrayList<String> getRivals() { return this.rivals; }

    public void setRivals( ArrayList<String> rivals) { this.rivals = rivals; }

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

    public ArrayList<String> getRivalsRoles() {
        return this.rivalsRoles;
    }

    public void setRivalsRoles(ArrayList<String> rivalsRoles) {
        this.rivalsRoles = rivalsRoles;
    }
}
