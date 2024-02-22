package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.DAO.FriendDAO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public ResponseEntity<ServerResponseDTO<List<UserDTO>>>
    searchUser( @RequestBody UserSearchRequestDTO userSearchRequestDTO)
    // The server response is a JSON message that contains the list of user, checking before that the requesterUser is 'OK'.
    {
        System.out.println("\nLoggedUserController: searchUser request from " +
                            "[" + userSearchRequestDTO.getRequesterUsername() + "] -> " +
                            "Searching [" + userSearchRequestDTO.getUsernameToSearch() + "]" + "\n");

        ServerResponseDTO<List<UserDTO>> userListResponse;
        HttpStatus responseHttp;
        boolean checkLogin = SessionManagement.getInstance().
                                isUserLogged(userSearchRequestDTO.
                                        getRequesterUsername());

        if(checkLogin) //Check if requesterUsername is logged or Not.
        {
            UserDAO userDAO = new UserDAO();
            List<UserDTO> userList = userDAO.globalSearchUser(userSearchRequestDTO.getUsernameToSearch());

            if (userList.isEmpty()) {
                System.out.println("\nLoggedUserController: - NOT FOUND - Database NOT contain the user: "
                                    + userSearchRequestDTO.getUsernameToSearch()+ "\n");

                userListResponse = new ServerResponseDTO<>(null);
                responseHttp = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(userListResponse, responseHttp);
            }

            System.out.println("\nLoggedUserController: - OK - Database contain the user: "
                                + userSearchRequestDTO.getUsernameToSearch() + "\n");

            userListResponse = new ServerResponseDTO<>(userList);
            responseHttp = HttpStatus.OK;
        }
        else
        {
            System.out.println("\nLoggedUserController: searchUser request from a NonLogged user\n");
            userListResponse = new ServerResponseDTO<>(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(userListResponse, responseHttp);
    }

    @PostMapping("/addFriend")
    @Override
    public ResponseEntity<ServerResponseDTO<Integer>> addFriend( @RequestBody FriendRequestDTO addFriendRequest)
    {
        ServerResponseDTO<Integer> addFriendResponse;
        HttpStatus responseHttp;
        boolean checkLogin = SessionManagement.getInstance().isUserLogged(addFriendRequest.getUsername());
        if(checkLogin)
        {
            FriendDAO me = new FriendDAO(addFriendRequest.getUsername());
            int friendRequestStatus = me.addFriend(addFriendRequest.getUsernameFriend());
            addFriendResponse = new ServerResponseDTO<>(friendRequestStatus);
            if(friendRequestStatus >= 0)
                responseHttp = HttpStatus.OK;
            else
                responseHttp = HttpStatus.BAD_REQUEST;
        }
        else
        {
            System.out.println("\nLoggedUserController: addFriend request from a NonLogged user\n");
            addFriendResponse = new ServerResponseDTO<>(-2);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(addFriendResponse, responseHttp);
    }
}
