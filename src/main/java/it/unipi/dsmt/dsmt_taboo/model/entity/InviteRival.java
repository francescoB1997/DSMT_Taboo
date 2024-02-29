package it.unipi.dsmt.dsmt_taboo.model.entity;

import it.unipi.dsmt.dsmt_taboo.model.DTO.InviteRivalRequestDTO;

public class InviteRival {

    private String gameId;
    private String userRival;
    private String userInviter;

    public InviteRival(InviteRivalRequestDTO inviteRival)
    {
        this.gameId = inviteRival.getGameId();
        this.userInviter = inviteRival.getUserInviter();
        this.userRival = inviteRival.getUserRival();
    }

    public String getGameId() {
        return this.gameId;
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
