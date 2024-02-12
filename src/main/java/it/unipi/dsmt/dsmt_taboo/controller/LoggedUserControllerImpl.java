package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.DAO.FriendDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.FriendDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.LoginRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LoggedUserControllerImpl implements LoggedUserControllerInterface
    // This class handle the action performed by a Logged User
{
    @PostMapping("/getFriendList")
    public ResponseEntity<ServerResponseDTO<List<FriendDTO>>> viewFriendList(String username)
    // The server response is a JSON message that contains a list of FriendDTO
    {
        FriendDAO friendDAO = new FriendDAO(username);
        ServerResponseDTO serverResponse = new ServerResponseDTO(friendDAO.getFriendList());
        return new ResponseEntity<>(serverResponse, HttpStatus.OK);
    }
}
