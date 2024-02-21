package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class UserSearchRequestDTO
{
    private String requesterUsername; // logged username
    private String usernameToSearch;  //

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
