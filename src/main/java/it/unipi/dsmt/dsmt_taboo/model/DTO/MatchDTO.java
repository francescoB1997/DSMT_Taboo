package it.unipi.dsmt.dsmt_taboo.model.DTO;

import it.unipi.dsmt.dsmt_taboo.model.entity.PendingMatchResult;

import java.util.ArrayList;

public class MatchDTO
{
    /**
     * This class represents a Data Transfer Object (DTO) used for encapsulating match information in the system.
     * It includes fields to store the match ID, the teams involved in the match (inviter and rival), their
     * corresponding roles, and the match result. The class provides constructors to initialize the match with
     * or without role information and methods to retrieve and set the match ID, teams, roles, and match scores.
     * Additionally, it provides a method to check if the match information is complete, i.e., if both inviter
     * and rival team scores are available.
     */

    private String matchId;
    private ArrayList<String> inviterTeam; // The initiator of the creation team
    private ArrayList<String> rolesInviterTeam;
    private ArrayList<String> rivalTeam;
    private ArrayList<String> rolesRivalTeam;
    private PendingMatchResult pendingMatchResult;

    public MatchDTO() { }

    public MatchDTO(String matchId, ArrayList<String> inviterTeam,
                    ArrayList<String> rivalTeam, Integer scoreInviterTeam,
                    Integer scoreRivalTeam)
    {
        this.matchId = matchId;
        this.inviterTeam = inviterTeam;
        this.rivalTeam = rivalTeam;
        this.pendingMatchResult = new PendingMatchResult(scoreInviterTeam, scoreRivalTeam);
    }

    public MatchDTO(String matchId, ArrayList<String> inviterTeam, ArrayList<String> rolesInviterTeam,
                    ArrayList<String> rivalTeam, ArrayList<String> rolesRivalTeam)
    {
        this.matchId = matchId;
        this.inviterTeam = inviterTeam;
        this.rolesInviterTeam = rolesInviterTeam;
        this.rivalTeam = rivalTeam;
        this.rolesRivalTeam = rolesRivalTeam;
        this.pendingMatchResult = new PendingMatchResult();
    }

    public String getMatchId() { return this.matchId; }

    public void setMatchId(String matchId) { this.matchId = matchId; }

    public ArrayList<String> getInviterTeam() { return this.inviterTeam; }

    public void setInviterTeam(ArrayList<String> inviterTeam) { this.inviterTeam = inviterTeam; }

    public ArrayList<String> getRivalTeam() { return this.rivalTeam; }

    public void setRivalTeam(ArrayList<String> rivalTeam) { this.rivalTeam = rivalTeam; }

    public Integer getScoreInviterTeam() { return this.pendingMatchResult.getScoreInviterTeam(); }

    public Integer getScoreRivalTeam() { return this.pendingMatchResult.getScoreRivalTeam(); }


    public void setScoreInviterTeam(Integer scoreInviterTeam) //Pay attention: the PendingMatch function can block you
    {
        this.pendingMatchResult.setScoreInviterTeam(scoreInviterTeam);
    }
    public void setScoreRivalTeam(Integer scoreRivalTeam) //Pay attention: the PendingMatch function can block you
    {
        this.pendingMatchResult.setScoreRivalTeam( scoreRivalTeam);
    }
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

    public Boolean infoMatchIsComplete()
    {
        return ( (this.getScoreRivalTeam() != null) && (this.getScoreInviterTeam() != null) );
    }

}
