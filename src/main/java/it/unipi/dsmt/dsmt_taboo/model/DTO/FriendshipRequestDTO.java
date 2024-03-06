package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class FriendshipRequestDTO
    // Represent a friendship request
{
    private String username;
    private String usernameFriend;

    public FriendshipRequestDTO(String username, String usernameFriend)
    {
        this.username = username;
        this.usernameFriend = usernameFriend;
    }

    public String getUsername() { return this.username; }

    public String getUsernameFriend() {
        return this.usernameFriend;
    }
}
