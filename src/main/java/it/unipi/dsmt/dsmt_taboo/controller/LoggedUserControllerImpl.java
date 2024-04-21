package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.DAO.FriendDAO;
import it.unipi.dsmt.dsmt_taboo.DAO.MatchDAO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import it.unipi.dsmt.dsmt_taboo.model.entity.*;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// +--------------------------------------------------------------------------------------------+
// |                The endpoints defined in this class handle all the action                   |
// |                performed by a Logged User.                                                 |
// +--------------------------------------------------------------------------------------------+

@RestController
public class LoggedUserControllerImpl implements LoggedUserControllerInterface
{
    @PostMapping("/getFriendList")
    @Override
    public ResponseEntity<ServerResponseDTO<List<FriendDTO>>> viewFriendList(@RequestBody String username)
    // This endpoint is used to retrieve the friend list (online and offline).
    // The server response will be a JSON message that contains a list of FriendDTO
    {
        System.out.println("getFriendList: request from [" + username + "]");
        ServerResponseDTO<List<FriendDTO>> getFriendListResponse;
        HttpStatus responseHttp;
        if (SessionManagement.getInstance().isUserLogged(username))  //Check if that user is logged
        {
            FriendDAO friendDAO = new FriendDAO(username);
            getFriendListResponse = new ServerResponseDTO<>(friendDAO.getFriendList());
            if(getFriendListResponse.getResponseMessage() == null)
                responseHttp = HttpStatus.BAD_REQUEST;
            else
                responseHttp = HttpStatus.OK;
        } else {
            getFriendListResponse = new ServerResponseDTO<>(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(getFriendListResponse, responseHttp);
    }

    @PostMapping("/searchUser")
    @Override
    public ResponseEntity<ServerResponseDTO<List<UserDTO>>>
        searchUser(@RequestBody UserSearchRequestDTO userSearchRequestDTO)
    // This endpoint permits to search all the users (not only friends) that matches the username inside the request.
    // The server response will be a JSON message that contains the list of user.
    {
        System.out.println("\nsearchUser: request from " +
                "[" + userSearchRequestDTO.getRequesterUsername() + "] -> " +
                "Searching [" + userSearchRequestDTO.getUsernameToSearch() + "]" + "\n");

        HttpStatus responseHttp;
        ServerResponseDTO<List<UserDTO>> userListResponse = new ServerResponseDTO<>(null);
        boolean checkLogin = SessionManagement.getInstance().
                isUserLogged(userSearchRequestDTO.getRequesterUsername());

        if (checkLogin) //Check if requesterUsername is logged or Not.
        {
            UserDAO userDAO = new UserDAO();
            List<UserDTO> userList = userDAO.globalSearchUser(userSearchRequestDTO.getUsernameToSearch());

            if(userList != null)
            {
                if (userList.isEmpty())
                {
                    System.out.println("\nsearchUser: NOT FOUND - Database NOT contains the user: "
                            + userSearchRequestDTO.getUsernameToSearch() + "\n");
                }
                else
                {
                    //System.out.println("\nsearchUser: OK - Database contains the user: "
                    //        + userSearchRequestDTO.getUsernameToSearch() + "\n");
                    userListResponse = new ServerResponseDTO<>(userList);
                }
                responseHttp = HttpStatus.OK;
            }
            else
                responseHttp = HttpStatus.BAD_REQUEST;
        }
        else
        {
            System.out.println("\nsearchUser: request from a NonLogged user\n");
            userListResponse = new ServerResponseDTO<>(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(userListResponse, responseHttp);
    }

    @PostMapping("/removeFriend")
    @Override
    public ResponseEntity<ServerResponseDTO<Integer>>
        removeFriend(@RequestBody FriendshipRequestDTO requesterUsername)
    // This endpoint allows to remove a friend by its friend list.
    {
        System.out.println("\nremoveFriend: Request from " +
                "[" + requesterUsername.getUsername() + "] -> " +
                "Removing [" + requesterUsername.getUsernameFriend() + "]" + "\n");

        HttpStatus responseHttp;
        ServerResponseDTO<Integer> removeFriendResponse;
        boolean checkLogin = SessionManagement.getInstance().
                isUserLogged(requesterUsername.getUsername());
        int requestStatus = 0;

        if (checkLogin)  //Check if that user is logged
        {
            FriendDAO friendDAO = new FriendDAO(requesterUsername.getUsername());
            boolean removeOpStatus = friendDAO.removeFriendDB(requesterUsername.
                    getUsernameFriend());
            if (removeOpStatus)
            {
                /* System.out.println("\nremoveFriend: The user "
                        + requesterUsername.getUsernameFriend() +
                        " has been successfully removed\n"); */
                removeFriendResponse = new ServerResponseDTO<>(requestStatus);
                responseHttp = HttpStatus.OK;

            }
            else
            {
                /* System.out.println("\nremoveFriend: Error occurred during remove operation." +
                        requesterUsername.getUsernameFriend()); */
                requestStatus++;
                removeFriendResponse = new ServerResponseDTO<>(requestStatus);
                responseHttp = HttpStatus.BAD_REQUEST;
            }
            return new ResponseEntity<>(removeFriendResponse, responseHttp);
        }
        else
        {
            System.out.println("\nremoveFriend: request from a NonLogged user\n");
            removeFriendResponse = new ServerResponseDTO<>(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(removeFriendResponse, responseHttp);
    }

    @PostMapping("/addFriend")
    @Override
    public ResponseEntity<ServerResponseDTO<Integer>> addFriend(@RequestBody FriendshipRequestDTO addFriendRequest)
    // This endpoint it's used to add a user as new friend.
    {
        ServerResponseDTO<Integer> addFriendResponse = null;
        HttpStatus responseHttp;
        boolean checkLogin = SessionManagement.getInstance().isUserLogged(addFriendRequest.getUsername());
        if (checkLogin)
        {
            FriendDAO me = new FriendDAO(addFriendRequest.getUsername());
            int friendRequestStatus = me.addFriend(addFriendRequest.getUsernameFriend());

            addFriendResponse = new ServerResponseDTO<>(friendRequestStatus);
            if (friendRequestStatus >= 0)
                responseHttp = HttpStatus.OK;
            else
                responseHttp = HttpStatus.BAD_REQUEST;
        }
        else
        {
            System.out.println("\naddFriend: request from a NonLogged user\n");
            addFriendResponse = new ServerResponseDTO<>(-2);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(addFriendResponse, responseHttp);
    }

    // The structure 'invites' stores the invitation requests
    Vector<InviteFriends> invites = new Vector<>();

    // This pendingMatchMap it's used to create a waitingRoom for the users that accepted the invitation, and they
    // are waiting the acceptation of the other participants.
    // BE ATTENTION, when all the invited users accepted the invitation, then the pendingMatch is "elevated" to runningMatch...
    final ConcurrentHashMap<String, PendingMatch> pendingMatchMap = new ConcurrentHashMap<>();

    // This runningMatch structure contains all the actual running matches. But the scores? See later...
    final ConcurrentHashMap<String, MatchDTO> runningMatch = new ConcurrentHashMap<>();

    @Async
    @PostMapping("/inviteFriends")
    @Override
    public ResponseEntity<ServerResponseDTO<String>> inviteFriends(@RequestBody InviteFriendRequestDTO request)
    // This endpoint it's used to store the invitation made by a user for its friend.
    // BE ATTENTION: this endpoint it's used by the user that want to start a match, BUT also by the Invited-Rival-User
    // that has accepted the invitation, and now informs the server about its team in order to complete all the
    // invited-user-information into server-side -> Remember also the Roles!
    {
        ServerResponseDTO<String> responseMessage = null;
        HttpStatus responseHttp;
        boolean checkLogin = SessionManagement.getInstance().isUserLogged(request.getUserInviter());
        if (checkLogin)
        {
            if (request.getGameId().isEmpty()) // If the gameId is empty, then this invite came from InviterUser.
            {
                request.setAutoGameId();
                invites.add(new InviteFriends(request));

                System.out.println("inviteFriends: Received invite from " + request.getUserInviter());

                PendingMatch myPendingMatch = new PendingMatch();
                pendingMatchMap.put(request.getGameId(), myPendingMatch);
                responseMessage = new ServerResponseDTO<>(request.getGameId());
            }
            else // Else, if the gameId is already setted, then it means the Rival sent this POST request
            {
                responseMessage = new ServerResponseDTO<>(request.getGameId());
                invites.removeIf((invite -> invite.getGameId().equals(request.getGameId()))); // remove the incomplete invite
                System.out.print("inviteFriends: Received invite from Rival " + request.getUserInviter());
                /* ONLY FOR DEBUG
                invites.forEach(invite ->
                {
                    assert (!invite.getGameId().equals(request.getGameId())); // DBG. Control that the old invite is truly removed.
                });
                 */

                invites.add(new InviteFriends(request)); //add the complete invite (with rivals list and rivalsRoles)
            }
            responseHttp = HttpStatus.OK;
        } else {
            System.out.println("\ninviteFriends: request from a NonLogged user\n");
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(responseMessage, responseHttp);
    }

    @Async
    @PostMapping("/checkInvite")
    @Override
    public ResponseEntity<ServerResponseDTO<InviteFriends>> checkInvite(@RequestBody String usernameRequester)
    // This endpoint checks if this requesterUser was invited to play
    {
        ServerResponseDTO<InviteFriends> receivedInvite = null;
        HttpStatus httpStatus = HttpStatus.OK;
        Boolean checkLogin = SessionManagement.getInstance().isUserLogged(usernameRequester);
        if (checkLogin)
        {
            for (InviteFriends invite : invites) // Search for any invite (inTeam or Rival)
            {
                for (String usernameRival : invite.getRivals())
                {
                    if (usernameRequester.equals(usernameRival))
                    {
                        receivedInvite = new ServerResponseDTO<>(invite);
                        break;
                    }
                }

                for (String usernameInTeam : invite.getYourTeam())
                {
                    if (usernameInTeam.equals(usernameRequester))
                    {
                        receivedInvite = new ServerResponseDTO<>(invite);
                        break;
                    }
                }
            }
        } else
            httpStatus = HttpStatus.UNAUTHORIZED;

        // -------------------- ONLY FOR PRINT MESSAGES --------------------
        if ((receivedInvite != null) && (httpStatus == HttpStatus.OK)) {
            System.out.println("checkInvite: Invite found for [" + usernameRequester + "] received by [" +
                    receivedInvite.getResponseMessage().getUserInviter() + "]");
        }
        else if (httpStatus == HttpStatus.UNAUTHORIZED)
            System.out.println("checkInvite: No logged user");
        else
            System.out.println("checkInvite: No invite found for [" + usernameRequester + "]");
        // ------------------------------------------------------------------
        return new ResponseEntity<>(receivedInvite, httpStatus);
    }

    @Async
    @PostMapping("/replyInvite")
    @Override
    public ResponseEntity<ServerResponseDTO<MatchDTO>> replyInvite(@RequestBody InviteReplyDTO replyInvite)
    // This endpoint handle the reply for an invitation (ACCEPT or REJECT).
    {
        ServerResponseDTO<MatchDTO> response = null;
        HttpStatus httpStatus = HttpStatus.OK;

        List<InviteFriends> tempList = invites.stream().filter(invite -> invite.getGameId().equals(replyInvite.getGameId())).collect(Collectors.toList());
        if(tempList != null) // If the server found the invite specified in the InviteReplyDTO...
        {
            InviteFriends r = tempList.get(0);
            if (!replyInvite.getInviteState()) // If the invite has been refused...
            {
                System.out.println("replyInvite: " + replyInvite.getSenderUsername() + " refused the invite made by " + r.getUserInviter() );
                invites.remove(r);
                pendingMatchMap.get(replyInvite.getGameId()).wakeUpAllThreads(); // wakeUp all the threads in waitingRoom
                pendingMatchMap.remove(replyInvite.getGameId()); // Free the memory for the old pendingMatch
            }
            else
            {
                System.out.println("replyInvite: " + replyInvite.getSenderUsername() + "accepted the invite made by " + r.getUserInviter());
                // WARNING: Those two 'addWaiting...' are BLOCKING-FUNCTION. Beacuse here is simulated the waitingRoom.
                if (replyInvite.getInvitedAsFriend())
                    pendingMatchMap.get(replyInvite.getGameId()).addWaitingFriend(replyInvite.getSenderUsername());
                else
                    pendingMatchMap.get(replyInvite.getGameId()).addWaitingRival(replyInvite.getSenderUsername());

                // When a thread awakes, it must understand if all the other player has accepted or not.
                PendingMatch pendingMatch = pendingMatchMap.get(replyInvite.getGameId()); // retrieve the PendingMatch
                if (pendingMatch != null)
                {
                    // BE ATTENTION: the 'tempList' update is NEEDED:
                    // -] In case of Rejection -> beacyse the pendingMatch will be removed from the refuser.
                    // -] In case all players have accepted the invite -> because there are thread that has the referece
                    //    to the incomplete pendingMatch.

                    tempList = invites.stream().filter(invite -> invite.getGameId().equals(replyInvite.getGameId())).collect(Collectors.toList());

                    MatchDTO matchDTO;
                    if(tempList != null && !tempList.isEmpty())
                    {
                        r = tempList.get(0);
                        matchDTO = new MatchDTO(replyInvite.getGameId(),
                                pendingMatch.getInviterTeamMember(), r.getRoles(),
                                pendingMatch.getRivalsTeamMember(), r.getRivalsRoles());

                        runningMatch.putIfAbsent(replyInvite.getGameId(), matchDTO); // The pendingMatch is elevated to RunningMatch
                    }
                    else
                    {
                        System.out.println("replyInvite: sending Refused Invitation MSG to " + replyInvite.getSenderUsername());
                        matchDTO = null;
                    }
                    response = new ServerResponseDTO<>(matchDTO);
                }
                else // The else means that, someone of the invited user has refused the invite
                {    // So each thread that was waiting in the latch, now awoken BUT because someone has rejected the invite
                    if (pendingMatch == null)
                        System.out.println("replyInvite: sending Refused Invitation MSG to " + replyInvite.getSenderUsername());
                }
            }
        }
        else
            httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, httpStatus);
    }

    @PostMapping("/getMyMatches")
    @Override
    public ResponseEntity<ServerResponseDTO<List<MatchDTO>>> getMyMatches(@RequestBody String usernameRequester)
    // This endpoint it's used to retrieve all played matches
    {
        System.out.println("getMyMatches: request from [" + usernameRequester + "]");
        HttpStatus responseHttp;
        ServerResponseDTO<List<MatchDTO>> getAllMatchesResponse = null;
        boolean checkLogin = SessionManagement.getInstance().isUserLogged(usernameRequester);
        if (checkLogin)
        {
            MatchDAO matchDAO = new MatchDAO();
            getAllMatchesResponse = new ServerResponseDTO<>(matchDAO.getMatches(usernameRequester));

            if(getAllMatchesResponse.getResponseMessage() == null)
                responseHttp = HttpStatus.BAD_REQUEST;
            else
                responseHttp = HttpStatus.OK;

        } else
            responseHttp = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(getAllMatchesResponse, responseHttp);
    }

    @PostMapping("/addNewMatch")
    @Override
    public ResponseEntity<ServerResponseDTO<Integer>> addNewMatch(@RequestBody MatchResultRequestDTO userMatchResult)
    // This endpoint it's used by the two Prompter to inform the server only by its score.
    {
        System.out.println("addNewMatch: " + userMatchResult.getMatchId() + " ScoreInv" + userMatchResult.getScoreInviterTeam() + " | ScoreRiv"
                + userMatchResult.getScoreRivalTeam() + " | Requester: " + userMatchResult.getUsernameRequester());

        HttpStatus responseHttp = HttpStatus.OK;
        ServerResponseDTO<Integer> addMatchResponse = new ServerResponseDTO<>(1);

        List<InviteFriends> r = invites.stream().filter(invite -> invite.getGameId().equals(userMatchResult.getMatchId())).collect(Collectors.toList());
        if (r != null && !r.isEmpty())
        {
            InviteFriends inviteToRemove = r.get(0);
            invites.remove(inviteToRemove);
        }

        MatchDTO matchInfo = runningMatch.get(userMatchResult.getMatchId());
        if (matchInfo != null)
        {
            if (userMatchResult.getScoreInviterTeam() != null)
            {
                System.out.println("addNewMatch: I'm the Inviter Prompter. We scored: " + userMatchResult.getScoreInviterTeam());

                // This function will block the thread if the server has not the complete information on both scores.
                matchInfo.setScoreInviterTeam(userMatchResult.getScoreInviterTeam());
            }
            else
            {
                System.out.println("addNewMatch: I'm the Rival Prompter. We scored: " + userMatchResult.getScoreRivalTeam());

                // This function will block the thread if the server has not the complete information on both scores.
                matchInfo.setScoreRivalTeam(userMatchResult.getScoreRivalTeam());
            }

            matchInfo = runningMatch.get(userMatchResult.getMatchId());

            if (matchInfo.infoMatchIsComplete())
            {
                System.out.println("addNewMatch: COMPLETE INFORMATION ON MATCH -> Inv[" + matchInfo.getScoreInviterTeam() + "] Riv[" + matchInfo.getScoreRivalTeam() + "]");
                MatchDAO matchDAO = new MatchDAO();
                boolean addOpStatus = matchDAO.addNewMatch(matchInfo);

                if (addOpStatus)
                {
                    System.out.println("addNewMatch: The match has been successfully added into DB");
                }
                else
                {
                    System.out.println("addNewMatch: Error occurred during adding operation:" +
                            "The match has NOT been added into DB\n");
                    addMatchResponse = new ServerResponseDTO<>(-1);
                    responseHttp = HttpStatus.BAD_REQUEST;
                }
            }
        }
        else
        {
            responseHttp = HttpStatus.BAD_REQUEST;
            addMatchResponse = new ServerResponseDTO<>(-2);
        }
        return new ResponseEntity<>(addMatchResponse, responseHttp);
    }

    @PostMapping("/getMatchResult")
    @Override
    public ResponseEntity<ServerResponseDTO<MatchResultRequestDTO>> getMatchResult(@RequestBody MatchResultRequestDTO matchResultRequestDTO)
    // This endpoint provide the scores-info about a match that just ended (IF THE SERVER HAS THIS INFO, ELSE...)
    {
        ServerResponseDTO<MatchResultRequestDTO> response;
        HttpStatus httpStatus;

        System.out.println("getMatchResult: IdMatch=" + matchResultRequestDTO.getMatchId() + "| DA= " +
                matchResultRequestDTO.getUsernameRequester());

        if(matchResultRequestDTO == null) // if the request matchId is Wrong...
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        MatchDTO matchDTO = runningMatch.get(matchResultRequestDTO.getMatchId());
        if(matchDTO == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        while(!matchDTO.infoMatchIsComplete())
        {
            // If the server still doesn't have the info, then the thread must wait.
            try { Thread.sleep(2000); } catch (Exception e) {}
            System.out.println("getMatchResult: i'm a thread waiting for the info (SCORES) completion");
        }

        MatchDAO matchDAO = new MatchDAO();
        MatchResultRequestDTO matchResult = matchDAO.getMatchResult(matchResultRequestDTO.getMatchId(), matchResultRequestDTO.getUsernameRequester());

        if(matchResult != null)
        {
            response = new ServerResponseDTO<>(matchResult);
            httpStatus  = HttpStatus.OK;
        }
        else
        {
            response = new ServerResponseDTO<>(null);
            httpStatus  = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(response, httpStatus);
    }
}