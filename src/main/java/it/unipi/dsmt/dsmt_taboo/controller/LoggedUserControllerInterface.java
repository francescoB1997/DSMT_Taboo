package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface LoggedUserControllerInterface
{
    public ResponseEntity<ServerResponseDTO<List<FriendDTO>>> viewFriendList(String username);
    public ResponseEntity<ServerResponseDTO<List<UserDTO>>> searchUser(UserSearchRequestDTO userSearchRequestDTO);
    public ResponseEntity<ServerResponseDTO<Integer>> removeFriend(FriendRequestDTO requesterUsername);
    public ResponseEntity<ServerResponseDTO<Integer>> addFriend( @RequestBody FriendRequestDTO addFriendRequest);
    public ResponseEntity<ServerResponseDTO<Integer>> createMatch( @RequestBody InviteFriendRequestDTO inviteRequest);
    public ResponseEntity<String> inviteFriendAsRival(@RequestBody InviteRivalRequestDTO request);
    public ResponseEntity<String> inviteFriendInTeam(@RequestBody InviteFriendRequestDTO request);

}
