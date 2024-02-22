package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

public interface LoggedUserControllerInterface
{
    public ResponseEntity<ServerResponseDTO<List<FriendDTO>>> viewFriendList(String username);
    public ResponseEntity<ServerResponseDTO<List<UserDTO>>> searchUser(UserSearchRequestDTO userSearchRequestDTO);

    public ResponseEntity<ServerResponseDTO<Integer>> addFriend( @RequestBody FriendRequestDTO addFriendRequest);

}
