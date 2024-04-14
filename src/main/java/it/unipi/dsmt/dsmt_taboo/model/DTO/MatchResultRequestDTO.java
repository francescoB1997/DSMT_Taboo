package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class MatchResultRequestDTO
{
    private String matchId;
    private String usernameRequester;
    private Integer scoreInviterTeam;
    private Integer scoreRivalTeam;

    public MatchResultRequestDTO(){}

    public MatchResultRequestDTO(String matchId, String usernameRequester, Integer scoreInviterTeam, Integer scoreRivalTeam)
    {
        this.matchId = matchId;
        this.usernameRequester = usernameRequester;
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

    public String getUsernameRequester() {
        return usernameRequester;
    }

    public void setUsernameRequester(String usernameRequester) {
        this.usernameRequester = usernameRequester;
    }
}
