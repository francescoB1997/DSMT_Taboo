package it.unipi.dsmt.dsmt_taboo.model.DTO;

import java.util.ArrayList;

public class MatchDTO
{
    private String matchId;
    private ArrayList<String> team1;
    private ArrayList<String> team2;
    private Integer scoreTeam1;
    private Integer scoreTeam2;

    public MatchDTO() { }

    public MatchDTO(String matchId,
                    ArrayList<String> team1, ArrayList<String> team2,
                    Integer scoreTeam1, Integer scoreTeam2)
    {
        this.matchId = matchId;
        this.team1 = team1;
        this.team1 = team2;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
    }

    public String getMatchId() { return this.matchId; }

    public void setMatchId(String matchId) { this.matchId = matchId; }

    public ArrayList<String> getTeam1() { return this.team1; }

    public void setTeam1(ArrayList<String> team1) { this.team1 = team1; }

    public ArrayList<String> getTeam2() { return this.team2; }

    public void setTeam2(ArrayList<String> team2) { this.team2 = team2; }

    public Integer getScoreTeam1() { return this.scoreTeam1; }

    public void setScoreTeam1(Integer scoreTeam1) { this.scoreTeam1 = scoreTeam1; }

    public Integer getScoreTeam2() { return this.scoreTeam2; }

    public void setScoreTeam2(Integer scoreTeam2) { this.scoreTeam2 = scoreTeam2; }


}
