package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface LoggedUserControllerInterface
{
    public ResponseEntity<ServerResponseDTO<List<FriendDTO>>> viewFriendList(String username);
    public ResponseEntity<ServerResponseDTO<List<UserDTO>>> searchUser(UserSearchRequestDTO userSearchRequestDTO);

}
