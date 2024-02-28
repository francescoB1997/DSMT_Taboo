package it.unipi.dsmt.dsmt_taboo.model.entity;

import it.unipi.dsmt.dsmt_taboo.model.DTO.InviteRivalRequestDTO;

public class InviteRival {

    private String id;
    private String userRival;
    private String userInviter;

    public InviteRival(InviteRivalRequestDTO inviteRival)
    {
        this.id = inviteRival.getGameId();
        this.userInviter = inviteRival.getUserInviter();
        this.userRival = inviteRival.getUserRival();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
