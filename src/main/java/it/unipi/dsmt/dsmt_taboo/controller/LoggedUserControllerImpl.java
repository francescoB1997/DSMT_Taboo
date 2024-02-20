package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.DAO.FriendDAO;
import it.unipi.dsmt.dsmt_taboo.DAO.SearchUserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.FriendDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerResponseDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.SearchedUserDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.UserSearchRequestDTO;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LoggedUserControllerImpl implements LoggedUserControllerInterface
    // This class handle the action performed by a Logged User
{
    @PostMapping("/getFriendList")
    @Override
    public ResponseEntity<ServerResponseDTO<List<FriendDTO>>> viewFriendList(@RequestBody String username)
    // The server response is a JSON message that contains a list of FriendDTO
    {
        System.out.println("LoggedUserController: getFriendList request from [" + username + "]");
        ServerResponseDTO getFriendListResponse;
        HttpStatus responseHttp;
        if(SessionManagement.getInstance().isUserLogged(username))  //Check if that user is logged
        {
            FriendDAO friendDAO = new FriendDAO(username);
            getFriendListResponse = new ServerResponseDTO(friendDAO.getFriendList());
            responseHttp = HttpStatus.OK;
        }
        else
        {
            getFriendListResponse = new ServerResponseDTO(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(getFriendListResponse, responseHttp);
    }

    @PostMapping("/searchUser")
    @Override
    public ResponseEntity<ServerResponseDTO<List<SearchedUserDTO>>> searchUser(@RequestBody UserSearchRequestDTO userSearchRequestDTO)
    // The server response is a JSON message that contains the list of user, checking before that the requesterUser is 'OK'.
    {
        System.out.println("LoggedUserController: searchUser request from [" + userSearchRequestDTO.getRequesterUser() + "] -> " +
                "Searching [" + userSearchRequestDTO.getUserToSearch() + "]");
        ServerResponseDTO<List<SearchedUserDTO>> userList;
        HttpStatus responseHttp;
        if(SessionManagement.getInstance().isUserLogged(userSearchRequestDTO.getRequesterUser()))  //Check if requesterUser is logged or Not.
        {
            SearchUserDAO searchUserDAO = new SearchUserDAO();
            userList = new ServerResponseDTO<>(searchUserDAO.getUserList(userSearchRequestDTO.getUserToSearch()));
            responseHttp = HttpStatus.OK;
        }
        else
        {
            System.out.println("LoggedUserController: searchUser request from a NonLogged user");
            userList = new ServerResponseDTO<>(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(userList, responseHttp);
    }
}
