package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class InviteReplyDTO
{
    private String senderUsername; // username who sends the reply
    private String gameId; // Cambiare con inviteId ???
    private Boolean invitedAsFriend;
    private Boolean inviteState;

    public InviteReplyDTO() {}

    public InviteReplyDTO(String senderUsername, String gameId, Boolean inviteState)
    {
        this.senderUsername = senderUsername;
        this.gameId = gameId;
        this.inviteState = inviteState;
    }

    public String getSenderUsername() { return this.senderUsername; }

    public void setRefuserUsername(String senderUsername) { this.senderUsername = senderUsername; }
    public String getGameId() { return this.gameId; }

    public void setRefusedGameId(String gameId) { this.gameId = gameId; }

    public Boolean getInviteState() { return this.inviteState; }

    public void setInviteState(Boolean inviteState) { this.inviteState = inviteState; }

    public Boolean getInvitedAsFriend() { return this.invitedAsFriend; }

    public void setInvitedAsFriend(Boolean invitedAsFriend) { this.invitedAsFriend = invitedAsFriend; }
}
