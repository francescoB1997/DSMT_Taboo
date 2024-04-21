package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class UserSearchRequestDTO
{
    /**
     * This class represents a Data Transfer Object (DTO) used for encapsulating user search
     * request data in the system. It includes fields for the username of the requester and
     * the username to be searched in the database. A constructor is provided to initialize
     * the object with these fields. Getter and setter methods are available for each field
     * to retrieve and update their values.
     */

    private String requesterUsername; // Logged username
    private String usernameToSearch;  // Username to search in the DB

    public UserSearchRequestDTO(String requesterUsername, String usernameToSearch)
    {
        this.requesterUsername = requesterUsername;
        this.usernameToSearch = usernameToSearch;
    }

    public String getRequesterUsername() { return this.requesterUsername; }
    public void setRequesterUsername(String requesterUsername) { this.requesterUsername = requesterUsername; }

    public String getUsernameToSearch() { return this.usernameToSearch; }
    public void setUsernameToSearch(String usernameToSearch) { this.usernameToSearch = usernameToSearch; }
}
