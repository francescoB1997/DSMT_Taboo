package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class FriendshipRequestDTO
{
    /**
     * This class represents a Data Transfer Object (DTO) used for encapsulating information about
     * a friendship request in the system. It contains fields to store the usernames of the user
     * sending the request and the user receiving the request. The class provides a constructor
     * to initialize its fields, as well as getter methods for accessing the usernames involved
     * in the friendship request.
     */

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
