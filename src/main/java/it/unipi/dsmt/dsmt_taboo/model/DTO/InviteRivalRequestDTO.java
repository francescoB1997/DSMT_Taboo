package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class InviteRivalRequestDTO  // Non utilizzata -> 08/03/2024
{
    private String gameId;
    private String userInviter;
    private String userRival;

    public InviteRivalRequestDTO(){}

    public InviteRivalRequestDTO(String gameId, String userInviter, String userRival)
    {
        this.gameId = gameId;
        this.userInviter = userInviter;
        this.userRival = userRival;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getUserInviter() {
        return userInviter;
    }

    public void setUserInviter(String userInviter) {
        this.userInviter = userInviter;
    }

    public String getUserRival() {
        return userRival;
    }

    public void setUserRival(String userRival) {
        this.userRival = userRival;
    }
}
