package it.unipi.dsmt.dsmt_taboo.model.DTO;

import java.util.ArrayList;

public class MatchDTO
{
    private String matchId;
    private ArrayList<String> inviterTeam; // THis is the initiator of the creation team
    private ArrayList<String> rolesInviterTeam;
    private ArrayList<String> rivalTeam;
    private ArrayList<String> rolesRivalTeam;
    private Integer scoreInviterTeam;
    private Integer scoreRivalTeam;

    public MatchDTO() { }

    public MatchDTO(String matchId,
                    ArrayList<String> inviterTeam, ArrayList<String> rolesInviterTeam,
                    ArrayList<String> rivalTeam, ArrayList<String> rolesRivalTeam,
                    Integer scoreInviterTeam, Integer scoreRivalTeam)
    {
        this.matchId = matchId;
        this.inviterTeam = inviterTeam;
        this.rolesInviterTeam = rolesInviterTeam;
        this.rivalTeam = rivalTeam;
        this.rolesRivalTeam = rolesRivalTeam;
        this.scoreInviterTeam = scoreInviterTeam;
        this.scoreRivalTeam = scoreRivalTeam;
    }

    public MatchDTO(String matchId, ArrayList<String> inviterTeam, ArrayList<String> rolesInviterTeam,
                    ArrayList<String> rivalTeam, ArrayList<String> rolesRivalTeam)
    {
        this.matchId = matchId;
        this.inviterTeam = inviterTeam;
        this.rolesInviterTeam = rolesInviterTeam;
        this.rivalTeam = rivalTeam;
        this.rolesRivalTeam = rolesRivalTeam;
        this.scoreInviterTeam = 0;
        this.scoreRivalTeam = 0;
    }

    public String getMatchId() { return this.matchId; }

    public void setMatchId(String matchId) { this.matchId = matchId; }

    public ArrayList<String> getInviterTeam() { return this.inviterTeam; }

    public void setInviterTeam(ArrayList<String> inviterTeam) { this.inviterTeam = inviterTeam; }

    public ArrayList<String> getRivalTeam() { return this.rivalTeam; }

    public void setRivalTeam(ArrayList<String> rivalTeam) { this.rivalTeam = rivalTeam; }

    public Integer getScoreInviterTeam() { return this.scoreInviterTeam; }

    public void setScoreInviterTeam(Integer scoreInviterTeam) { this.scoreInviterTeam = scoreInviterTeam; }

    public Integer getScoreRivalTeam() { return this.scoreRivalTeam; }

    public void setScoreRivalTeam(Integer scoreRivalTeam) { this.scoreRivalTeam = scoreRivalTeam; }


    public ArrayList<String> getRolesInviterTeam() {
        return this.rolesInviterTeam;
    }

    public void setRolesInviterTeam(ArrayList<String> rolesInviterTeam) {
        this.rolesInviterTeam = rolesInviterTeam;
    }

    public ArrayList<String> getRolesRivalTeam() {
        return this.rolesRivalTeam;
    }

    public void setRolesRivalTeam(ArrayList<String> rolesRivalTeam) {
        this.rolesRivalTeam = rolesRivalTeam;
    }
}
