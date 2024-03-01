package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.DAO.FriendDAO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import it.unipi.dsmt.dsmt_taboo.model.entity.InviteInTeam;
import it.unipi.dsmt.dsmt_taboo.model.entity.InviteRival;
import it.unipi.dsmt.dsmt_taboo.model.entity.TeamCreationWaiting;
import it.unipi.dsmt.dsmt_taboo.model.entity.RivalWaiting;
import it.unipi.dsmt.dsmt_taboo.service.UserService;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

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

    @PostMapping("/createMatch")
    @Override
    public ResponseEntity<ServerResponseDTO<Integer>> createMatch( @RequestBody InviteFriendRequestDTO inviteRequest)
    {
        // TO DO
        return null;
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

        HttpStatus responseHttp;
        ServerResponseDTO<List<UserDTO>> userListResponse;
        boolean checkLogin = SessionManagement.getInstance().
                             isUserLogged(userSearchRequestDTO.getRequesterUsername());

        if(checkLogin) //Check if requesterUsername is logged or Not.
        {
            UserDAO userDAO = new UserDAO();
            List<UserDTO> userList = userDAO.globalSearchUser(userSearchRequestDTO.
                                                                getUsernameToSearch());
            if(userList.isEmpty())
            {
                System.out.println("\nLoggedUserController: - NOT FOUND - Database NOT contain the user: "
                                    + userSearchRequestDTO.getUsernameToSearch()+ "\n");
                userListResponse = new ServerResponseDTO<>(null);

            } else {
                System.out.println("\nLoggedUserController: - OK - Database contain the user: "
                                    + userSearchRequestDTO.getUsernameToSearch() + "\n");
                userListResponse = new ServerResponseDTO<>(userList);
            }

            responseHttp = HttpStatus.OK;
            return new ResponseEntity<>(userListResponse, responseHttp);

        } else {
            System.out.println("\nLoggedUserController: searchUser request from a NonLogged user\n");
            userListResponse = new ServerResponseDTO<>(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(userListResponse, responseHttp);
    }

    @PostMapping("/removeFriend")
    @Override
    public ResponseEntity<ServerResponseDTO<Integer>>
    removeFriend(@RequestBody FriendRequestDTO requesterUsername)
    {
        System.out.println("\nLoggedUserController: removeUser request from " +
                "[" + requesterUsername.getUsername() + "] -> " +
                "Removing [" + requesterUsername.getUsernameFriend() + "]" + "\n");

        HttpStatus responseHttp;
        ServerResponseDTO<Integer> removeFriendResponse;
        boolean checkLogin = SessionManagement.getInstance().
                             isUserLogged(requesterUsername.getUsername());
        int requestStatus = 0;

        if(checkLogin)  //Check if that user is logged
        {
            FriendDAO friendDAO = new FriendDAO(requesterUsername.getUsername());
            boolean removeOpStatus = friendDAO.removeFriendDB(requesterUsername.
                                                                  getUsernameFriend());
            if(removeOpStatus)
            {
                System.out.println("\nThe user "
                                        + requesterUsername.getUsernameFriend() +
                                        " has been successfully removed\n");
                removeFriendResponse = new ServerResponseDTO<>(requestStatus);
                responseHttp = HttpStatus.OK;

            } else {
                System.out.println("\nError occurred during remove operation." +
                                        requesterUsername.getUsernameFriend() +
                                        " has NOT been removed from your friend list\n");
                requestStatus++;
                removeFriendResponse = new ServerResponseDTO<>(requestStatus);
                responseHttp = HttpStatus.BAD_REQUEST;
            }

            return new ResponseEntity<>(removeFriendResponse, responseHttp);

        } else {
            System.out.println("\nLoggedUserController: searchUser request from a NonLogged user\n");
            removeFriendResponse = new ServerResponseDTO<>(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(removeFriendResponse, responseHttp);
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

    UserService userService = new UserService();

    Vector<InviteInTeam> invitesForTeam = new Vector<>();
    final HashMap<String, TeamCreationWaiting> yourTeamMap = new HashMap<>();
    @Async
    @PostMapping("/inviteInTeam")
    @Override
    public ResponseEntity<String>
    inviteFriendInTeam(@RequestBody InviteFriendRequestDTO request)
    {
        System.out.println("Invito: Requester[" + request.getUserInviter() + "] , IDRequest[" + request.getGameId() + "]");
        request.getYourTeam().forEach(friendUsername -> System.out.print("[" + friendUsername + "] "));
        System.out.println();
        request.getRoles().forEach(roleFriend -> System.out.print("[" + roleFriend + "] "));
        System.out.println("Rival:[" + request.getUserRival() + "]");

        /* DA VERIFICARE COME PROCEDERE CON LA MEMORIZZAZIONE DEGLI UTENTI IN ATTESA */
        invitesForTeam.add(new InviteInTeam(request));

        synchronized (yourTeamMap)
        {
            TeamCreationWaiting playersWaiting = new TeamCreationWaiting(0, 0, 0);
            yourTeamMap.put(request.getGameId(), playersWaiting);
        }

        return new ResponseEntity<>("correct invite", HttpStatus.OK);
    }

    Vector<InviteRival> invitesForRival = new Vector<>();
    HashMap<String, RivalWaiting> rivalMap = new HashMap<>();
    @Async
    @PostMapping("/inviteRival")
    @Override
    public ResponseEntity<String>
    inviteFriendAsRival(@RequestBody InviteRivalRequestDTO request) {
        invitesForRival.add(new InviteRival(request));

        synchronized (rivalMap)
        {
            RivalWaiting playersWaiting =
                    new RivalWaiting(0);
            rivalMap.put(request.getGameId(), playersWaiting);
        }

        return new ResponseEntity<>("correct invite", HttpStatus.OK);
    }
}

