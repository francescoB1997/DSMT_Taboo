package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class MatchResultRequestDTO
{
    /**
     * This class represents a Data Transfer Object (DTO) used for encapsulating match result request
     * information in the system. It includes fields to store the match ID, the username of the requester,
     * and the scores of the inviter and rival teams. The class provides constructors to initialize the match
     * result request with or without score information and methods to retrieve and set the match ID,
     * requester username, and team scores.
     */

    private String matchId;
    private String usernameRequester;
    private Integer scoreInviterTeam;
    private Integer scoreRivalTeam;

    public MatchResultRequestDTO(){}

    public MatchResultRequestDTO(String matchId, String usernameRequester,
                                 Integer scoreInviterTeam, Integer scoreRivalTeam)
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
