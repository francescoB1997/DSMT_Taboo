package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.DAO.FriendDAO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
public class LoggedUserControllerImpl implements LoggedUserControllerInterface
    // This class handle the action performed by a Logged User
{
    @Autowired
    SessionManagement session;

    @PostMapping("/getFriendList")
    @Override
    public ResponseEntity<ServerResponseDTO<List<FriendDTO>>> viewFriendList(@RequestBody String username)
    // The server response is a JSON message that contains a list of FriendDTO
    {
        System.out.println("LoggedUserController: getFriendList request from [" + username + "]");
        ServerResponseDTO<List<FriendDTO>> getFriendListResponse;
        HttpStatus responseHttp;
        if(SessionManagement.getInstance().isUserLogged(username))  //Check if that user is logged
        {
            FriendDAO friendDAO = new FriendDAO(username);
            getFriendListResponse = new ServerResponseDTO<>(friendDAO.getFriendList());
            responseHttp = HttpStatus.OK;
        }
        else
        {
            getFriendListResponse = new ServerResponseDTO<>(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(getFriendListResponse, responseHttp);
    }

    @PostMapping("/searchUser")
    @Override
    public ResponseEntity<ServerResponseDTO<List<UserDTO>>> searchUser(@RequestBody UserSearchRequestDTO userSearchRequestDTO)
    // The server response is a JSON message that contains the list of user, checking before that the requesterUser is 'OK'.
    {
        System.out.println("LoggedUserController: searchUser request from [" + userSearchRequestDTO.getRequesterUsername() + "] -> " +
                "Searching [" + userSearchRequestDTO.getUsernameToSearch() + "]");
        ServerResponseDTO<List<UserDTO>> userListResponse;
        HttpStatus responseHttp;
        if(SessionManagement.getInstance().isUserLogged(userSearchRequestDTO.getRequesterUsername()))  //Check if requesterUsername is logged or Not.
        {
            UserDAO userDAO = new UserDAO();
            userListResponse = new ServerResponseDTO<>(userDAO.globalSearchUser(userSearchRequestDTO.getUsernameToSearch()));
            responseHttp = HttpStatus.OK;
        }
        else
        {
            System.out.println("LoggedUserController: searchUser request from a NonLogged user");
            userListResponse = new ServerResponseDTO<>(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(userListResponse, responseHttp);
    }

}
