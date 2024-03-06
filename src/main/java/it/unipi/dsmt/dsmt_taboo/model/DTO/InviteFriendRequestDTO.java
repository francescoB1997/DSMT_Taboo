package it.unipi.dsmt.dsmt_taboo.model.DTO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class InviteFriendRequestDTO //InviteFriendRequestDTO
{
    private String gameId;
    private ArrayList<String> yourTeam;
    private ArrayList<String> roles;
    private String userInviter;
    private ArrayList<String> rivals;

    public InviteFriendRequestDTO() {}

    public InviteFriendRequestDTO(String gameId, ArrayList<String> yourTeam,
                                  ArrayList<String> roles, String userInviter,  ArrayList<String> userRival)
    {
        this.gameId = this.getUniqueGameId();
        this.yourTeam = yourTeam;
        this.roles = roles;
        this.userInviter = userInviter;
        this.rivals = userRival;
    }

    public String getGameId()
    {
        if(gameId == "")
            this.gameId = getUniqueGameId();
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
}
