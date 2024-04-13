package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class MatchResultRequestDTO
{
    private String matchId;
    private Integer scoreInviterTeam;
    private Integer scoreRivalTeam;

    public MatchResultRequestDTO(){}

    public MatchResultRequestDTO(String matchId, Integer scoreInviterTeam, Integer scoreRivalTeam)
    {
        this.matchId = matchId;
        this.scoreRivalTeam = scoreRivalTeam;
        this.scoreInviterTeam = scoreInviterTeam;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public Integer getScoreInviterTeam() {
        return scoreInviterTeam;
    }

    public void setScoreInviterTeam(Integer scoreInviterTeam) {
        this.scoreInviterTeam = scoreInviterTeam;
    }

    public Integer getScoreRivalTeam() {
        return scoreRivalTeam;
    }

    public void setScoreRivalTeam(Integer scoreRivalTeam) {
        this.scoreRivalTeam = scoreRivalTeam;
    }

}
