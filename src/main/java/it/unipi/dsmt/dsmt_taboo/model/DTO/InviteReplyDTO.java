package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class InviteReplyDTO
{
    /**
     * This class represents a Data Transfer Object (DTO) used for encapsulating information about
     * a reply to a friend invitation in the system. It contains fields to store details such as the
     * sender's username, the game ID associated with the invitation, the state of the
     * invitation (accepted or declined), and whether the invitation was sent as a friend request.
     * The class provides constructors to initialize its fields, as well as getter and setter methods
     * for accessing and modifying the reply details.
     */

    private String senderUsername; // username who sends the reply
    private String gameId;
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
