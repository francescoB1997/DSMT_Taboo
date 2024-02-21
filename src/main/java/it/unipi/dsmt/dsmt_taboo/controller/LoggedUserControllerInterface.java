package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.FriendDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.FriendRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.LoginRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

public interface LoggedUserControllerInterface
{
    public ResponseEntity<ServerResponseDTO<List<FriendDTO>>> viewFriendList(String username);
    public ResponseEntity<ServerResponseDTO<String>> removeFriendRequest(FriendRequestDTO removeRequest);
}
