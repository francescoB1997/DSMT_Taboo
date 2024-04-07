package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import it.unipi.dsmt.dsmt_taboo.model.entity.InviteFriends;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface LoggedUserControllerInterface
{
    public ResponseEntity<ServerResponseDTO<List<FriendDTO>>> viewFriendList(String username);
    public ResponseEntity<ServerResponseDTO<List<UserDTO>>> searchUser(UserSearchRequestDTO userSearchRequestDTO);
    public ResponseEntity<ServerResponseDTO<Integer>> removeFriend(FriendshipRequestDTO requesterUsername);
    public ResponseEntity<ServerResponseDTO<Integer>> addFriend( @RequestBody FriendshipRequestDTO addFriendRequest);
    public ResponseEntity<ServerResponseDTO<Integer>> createMatch( @RequestBody InviteFriendRequestDTO inviteRequest);
    public ResponseEntity<ServerResponseDTO<String>> inviteFriends(@RequestBody InviteFriendRequestDTO request);
    public ResponseEntity<ServerResponseDTO<InviteFriends>> checkInvite(@RequestBody String usernameRequester);
    public ResponseEntity<ServerResponseDTO<List<MatchDTO>>> getMyMatches(@RequestBody String usernameRequester);
    public ResponseEntity<ServerResponseDTO<MatchDTO>> replyInvite(@RequestBody InviteReplyDTO refusedInvite);
    public ResponseEntity<ServerResponseDTO<Integer>> addNewMatch(@RequestBody MatchDTO match);

}
