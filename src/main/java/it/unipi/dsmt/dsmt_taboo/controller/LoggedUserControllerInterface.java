package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.FriendDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.SearchedUserDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerResponseDTO;
import org.springframework.http.ResponseEntity;
import it.unipi.dsmt.dsmt_taboo.model.DTO.UserSearchRequestDTO;

import java.util.List;

public interface LoggedUserControllerInterface
{
    public ResponseEntity<ServerResponseDTO<List<FriendDTO>>> viewFriendList(String username);
    public ResponseEntity<ServerResponseDTO<List<SearchedUserDTO>>> searchUser(UserSearchRequestDTO userSearchRequestDTO);

}
