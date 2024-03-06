package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.DAO.FriendDAO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import it.unipi.dsmt.dsmt_taboo.model.entity.InviteFriends;
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
    removeFriend(@RequestBody FriendshipRequestDTO requesterUsername)
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
    public ResponseEntity<ServerResponseDTO<Integer>> addFriend( @RequestBody FriendshipRequestDTO addFriendRequest)
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

    Vector<InviteFriends> invites = new Vector<>();
    final HashMap<String, TeamCreationWaiting> yourTeamMap = new HashMap<>();
    @Async
    @PostMapping("/inviteFriends")
    @Override
    public ResponseEntity<ServerResponseDTO<String>> inviteFriendInTeam(@RequestBody InviteFriendRequestDTO request)
    {
        ServerResponseDTO<String> responseMessage = null;
        HttpStatus responseHttp;
        boolean checkLogin = SessionManagement.getInstance().isUserLogged(request.getUserInviter());
        if(checkLogin)
        {
            System.out.println("Invito: Requester[" + request.getUserInviter() + "] , IDRequest[" + request.getGameId() + "]");
            request.getYourTeam().forEach(friendUsername -> System.out.print("[" + friendUsername + "] "));
            System.out.println();
            request.getRoles().forEach(roleFriend -> System.out.print("[" + roleFriend + "] "));
            System.out.println("Rival:[" + request.getRivals().get(0) + "]");

            /* DA VERIFICARE COME PROCEDERE CON LA MEMORIZZAZIONE DEGLI UTENTI IN ATTESA */
            invites.add(new InviteFriends(request));

            synchronized (yourTeamMap)
            {
                TeamCreationWaiting playersWaiting = new TeamCreationWaiting(0, 0, 0);
                yourTeamMap.put(request.getGameId(), playersWaiting);
            }

            responseMessage = new ServerResponseDTO<>("correct invite");
            responseHttp = HttpStatus.OK;
        }
        else
        {
            System.out.println("\nLoggedUserController: addFriend request from a NonLogged user\n");
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(responseMessage, responseHttp);
    }

    Vector<InviteRival> invitesForRival = new Vector<>();
    HashMap<String, RivalWaiting> rivalMap = new HashMap<>();
    @Async
    @PostMapping("/inviteRival")
    @Override
    public ResponseEntity<String>
    inviteFriendAsRival(@RequestBody InviteRivalRequestDTO request)
    {
        invitesForRival.add(new InviteRival(request));

        synchronized (rivalMap)
        {
            RivalWaiting playersWaiting =
                    new RivalWaiting(0);
            rivalMap.put(request.getGameId(), playersWaiting);

        }

        return new ResponseEntity<>("correct invite", HttpStatus.OK);
    }

    /*
        Gestire l'invito con due classi separate, ci obbliga poi a dover effettuare un doppio checkInvite,
        uno per gli inviteFriend e inviteRival. Quindi se abbiamo ricevuto entrambi, quale dei due proponiamo prima?
        Inoltre, bisogna anche specificare qual è il tipo di ritorno dell'invito -> Quindi o facciamo un tipo generico
        con cui rispondere dall'endpoint 'checkReceivedInvite', oppure possiamo fare due endPoint diversi per controllare
        i due inviti (FriendInvite e RivalInvite).
*/

    @Async
    @PostMapping("/checkInvite")
    @Override public ResponseEntity<ServerResponseDTO<InviteFriends>>checkInvite(@RequestBody String usernameRequester)
    {
        ServerResponseDTO<InviteFriends> receivedInvite = null;
        HttpStatus httpStatus = HttpStatus.OK;
        boolean checkLogin = SessionManagement.getInstance().isUserLogged(usernameRequester);
        if(checkLogin)
        {
            for (InviteFriends invite : invites) // Search for any invite (inTeam or Rival)
            {
                if(usernameRequester.equals(invite.getRivals().get(0)))
                {
                    receivedInvite = new ServerResponseDTO<>(invite);
                    break;
                }

                for(String usernameInTeam : invite.getYourTeam())
                {
                    receivedInvite = new ServerResponseDTO<>(invite);
                    break;
                }
            }
        }
        else
            httpStatus = HttpStatus.UNAUTHORIZED;

        if(receivedInvite == null && (httpStatus != HttpStatus.UNAUTHORIZED))
            System.out.println("No Inviti per [" + usernameRequester + "]");
        else
            System.out.println("Invito per [" + usernameRequester + "] ricevuto da [" + receivedInvite.getResponseMessage().getUserInviter() + "]");


        return new ResponseEntity<>(receivedInvite, httpStatus);
    }


}

