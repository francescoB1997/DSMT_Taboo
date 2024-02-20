package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class UserSearchRequestDTO
{
    private String requesterUser;
    private String userToSearch;

    public UserSearchRequestDTO(String requesterUser, String userToSearch)
    {
        this.requesterUser = requesterUser;
        this.userToSearch = userToSearch;
    }

    public String getRequesterUser() { return this.requesterUser; }
    public void setRequesterUser(String requesterUser) { this.requesterUser = requesterUser; }

    public String getUserToSearch() { return userToSearch; }
    public void setUserToSearch(String userToSearch) { this.userToSearch = userToSearch; }
}
