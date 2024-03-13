package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import it.unipi.dsmt.dsmt_taboo.model.entity.InviteFriends;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface LoggedUserControllerInterface
{
    public ResponseEntity<ServerResponseDTO<List<FriendDTO>>> viewFriendList(String username);
    public ResponseEntity<ServerResponseDTO<List<UserDTO>>> searchUser(UserSearchRequestDTO userSearchRequestDTO);
    public ResponseEntity<ServerResponseDTO<Integer>> removeFriend(FriendshipRequestDTO requesterUsername);
    public ResponseEntity<ServerResponseDTO<Integer>> addFriend( @RequestBody FriendshipRequestDTO addFriendRequest);
    public ResponseEntity<ServerResponseDTO<Integer>> createMatch( @RequestBody InviteFriendRequestDTO inviteRequest);

    // public ResponseEntity<String> inviteFriendAsRival(@RequestBody InviteRivalRequestDTO request); // Commentato il 08/03/2024
    public ResponseEntity<ServerResponseDTO<String>> inviteFriends(@RequestBody InviteFriendRequestDTO request);
    public ResponseEntity<ServerResponseDTO<InviteFriends>> checkInvite(@RequestBody String usernameRequester);

    public ResponseEntity<ServerResponseDTO<String>> replyInvite(@RequestBody InviteReplyDTO refusedInvite);

}
