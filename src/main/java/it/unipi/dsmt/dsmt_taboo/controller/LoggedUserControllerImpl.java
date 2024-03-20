package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.DAO.FriendDAO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import it.unipi.dsmt.dsmt_taboo.model.entity.*;
import it.unipi.dsmt.dsmt_taboo.service.UserService;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
public class LoggedUserControllerImpl implements LoggedUserControllerInterface
    // This class handle all the action performed by a Logged User
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
    final ConcurrentHashMap<String, PendingMatch> pendingMatchMap = new ConcurrentHashMap<>();
    final ConcurrentHashMap<String, MatchDTO> runningMatch = new ConcurrentHashMap<>();

    @Async
    @PostMapping("/inviteFriends")
    @Override
    public ResponseEntity<ServerResponseDTO<String>> inviteFriends(@RequestBody InviteFriendRequestDTO request)
    {
        ServerResponseDTO<String> responseMessage = null;
        HttpStatus responseHttp;
        boolean checkLogin = SessionManagement.getInstance().isUserLogged(request.getUserInviter());
        if(checkLogin)
        {
            if(request.getGameId().isEmpty()) // If it is first time that i receive that invite...
            {
                request.setAutoGameId();
                invites.add(new InviteFriends(request));

                PendingMatch myPendingMatch = new PendingMatch();
                pendingMatchMap.put(request.getGameId(), myPendingMatch);

                responseMessage = new ServerResponseDTO<>(request.getGameId());
            }
            else // Else, if the gameId is already setted, then it means the Rival sent this POST
            {
                System.out.print("<R> ");
                responseMessage = new ServerResponseDTO<>("correct rival invite");
                invites.removeIf((invite ->  invite.getGameId().equals(request.getGameId()))); // remove the incomplete invite
                invites.forEach(invites ->
                {
                    assert(!invites.getGameId().equals(request.getGameId())); // DBG. Se va storto, la remove non funziona!
                });
                invites.add(new InviteFriends(request)); //add the complete invite (with rivals list)
                request.printInfoInvite();
            }
            responseHttp = HttpStatus.OK;
        }
        else
        {
            System.out.println("\nLoggedUserController: addFriend request from a NonLogged user\n");
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(responseMessage, responseHttp);
    }

    @Async
    @PostMapping("/checkInvite")
    @Override public ResponseEntity<ServerResponseDTO<InviteFriends>>checkInvite(@RequestBody String usernameRequester)
    {
        ServerResponseDTO<InviteFriends> receivedInvite = null;
        HttpStatus httpStatus = HttpStatus.OK;
        Boolean checkLogin = SessionManagement.getInstance().isUserLogged(usernameRequester);
        if(checkLogin)
        {
            for (InviteFriends invite : invites) // Search for any invite (inTeam or Rival)
            {
                for(String usernameRival : invite.getRivals())
                {
                    if(usernameRequester.equals(usernameRival))
                    {
                        receivedInvite = new ServerResponseDTO<>(invite);
                        //System.out.println("True in if RIVAL");
                        break;
                    }
                }

                for(String usernameInTeam : invite.getYourTeam())
                {
                    if(usernameInTeam.equals(usernameRequester))
                    {
                        receivedInvite = new ServerResponseDTO<>(invite);
                        break;
                    }
                }
            }
        }
        else
            httpStatus = HttpStatus.UNAUTHORIZED;

        // -------------------- ONLY FOR DEBUG --------------------
        if( (receivedInvite != null) && (httpStatus == HttpStatus.OK))
        {
            System.out.println("Invite found for [" + usernameRequester + "] received by [" +
                    receivedInvite.getResponseMessage().getUserInviter() + "]");
        }
        else if(httpStatus == HttpStatus.UNAUTHORIZED)
            System.out.println("No logged user");
        else
            System.out.println("No invite found for [" + usernameRequester + "]");
        // ----------------------------------------------------------
        return new ResponseEntity<>(receivedInvite, httpStatus);
    }

    @Async
    @PostMapping("/replyInvite")
    @Override
    public ResponseEntity<ServerResponseDTO<MatchDTO>> replyInvite(@RequestBody InviteReplyDTO replyInvite)
        // This function handle the replyInvite. It necessary to know which is the Invite and who is the refuser
    {
        ServerResponseDTO<MatchDTO> response = null;
        HttpStatus httpStatus = HttpStatus.OK;
        InviteFriends r =  invites.stream().filter(invite -> invite.getGameId().equals(replyInvite.getGameId())).toList().get(0);
        if(!replyInvite.getInviteState()) // If the invite has been refused...
        {
            System.out.println("replyInvite:  [" + replyInvite.getSenderUsername() + "] ha rifiutato l'invito di [" + r.getUserInviter() + "]");
            //invites.removeIf(invite -> invite.getGameId().equals(replyInvite.getGameId()));
            invites.remove(r);
            // -------------------- ONLY FOR DEBUG --------------------
            invites.forEach(invites ->
            {
                assert(!invites.getGameId().equals(replyInvite.getGameId())); // DBG. Se va storto, la remove non funziona!
            });
            // ----------------------------------------------------------
            pendingMatchMap.get(replyInvite.getGameId()).wakeUpAllThreads();
            //response = new ServerResponseDTO<>(0); // The 0, means that someone have refused the invite
            pendingMatchMap.remove(replyInvite.getGameId()); // Free the memory for the old pendingMatch
        }
        else
        {
            System.out.println("replyInvite:  [" + replyInvite.getSenderUsername() + "] ha accettato l'invito di [" + r.getUserInviter() + "]");

            // Metto in attesa il replySender
            if(replyInvite.getInvitedAsFriend())
                pendingMatchMap.get(replyInvite.getGameId()).addWaitingFriend(replyInvite.getSenderUsername());
            else
                pendingMatchMap.get(replyInvite.getGameId()).addWaitingRival(replyInvite.getSenderUsername());

            PendingMatch pendingMatch = pendingMatchMap.get(replyInvite.getGameId());
            if(pendingMatch != null)
            {
                MatchDTO matchDTO = new MatchDTO(replyInvite.getGameId(), pendingMatch.getInviterTeamMember(),
                        pendingMatch.getRivalsTeamMember());

                System.out.println("matchDTO = { InviterTeam = [ " + pendingMatch.getInviterTeamMember() + " ] \nRivalTeam = [ " + pendingMatch.getRivalsTeamMember() + " ]}");

                MatchDTO returned = runningMatch.putIfAbsent(replyInvite.getGameId(), matchDTO); // Il returned è solo per DGB

                // -------------------- ONLY FOR DEBUG --------------------
                if(returned != null)
                    System.out.println("Thread: esisteva già il MatchDTO nei runningMatch");
                else
                    System.out.println("THread: *** questo messaggio dovrei vederla una volta ***");
                // ----------------------------------------------------------
                response = new ServerResponseDTO<>(matchDTO); // The 1, means that all of users have accepted the invite
            }
            else // The else means that, someone of the invited user has refused the invite"
            {
                System.out.println("Invito rifiutato");
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

/*
    ************************************************ 08/03/2024 ********************************************************
        PROGRESSI:
            -] Creata e gestita la pagina che permette al primo Rival di crearsi il suo Team,
               nascondendo gli utenti già invitati.
            -] Il primo Rival può quindi creare il suo team esattamente nello stesso modo in cui lo ha fatto
               l'Inviter.
            -] L'invito fatto dall'Inviter viene correttamente aggiornato nell'istante in cui il (primo) rival
               crea la sua squadra --> Ciò è necessario per informare lato Javascript e Java CHI sono questi rivali
               e quindi renderli consci (quando eseguono la checkInvite) che sono stati invitati.
               Ho sfruttato lo stesso endpoint /inviteFriend anche quando il Rival costruisce la propria
               squadra, visto che per il rival è un invito che fa ai SUOI amici -> Vedi IF nel PostMapping.
            -] Adesso, ogni giocatore che effettua la checkInvite, capisce perfettamente di essere stato invitato,
               da chi, e se è in squadra Blu(dell'Inviter) o Rossa(del Rival) -> Gestito nel Javascript del checkInvite.

        DA FARE:
            -] Gestire il rifiuto dell'invito lato Javascript e Java(PostMapping).    --------->    FATTO il 10/03/2024
            -] Bisogna gestire il passo successivo, ossia l'attesa dei giocatori.     --------->    FATTO il 11/03/2024
            -] Aggiornare quindi i playersWaiting che "accettano" l'invito.           --------->    FATTO il 11/03/2024
            -] Direi di aggiungere un checkInvite anche se si clicca su CreateYourTeam in modo tale da dire
               all'utente --> "Prima di crearti un tuo team, sei già stato invitato -> Che fai?"
               obbligandolo quindi ad ACCETTARE o RIFIUTARE.
               Perchè altrimenti questo utente potrebbe creare un invito che riguarda utenti già invitati ed in
               attesa di altri -> creando inviti annidati! <(o_O)>
  ****************************************** ****************************************** ********************************


  ************************************************** 10/03/2024 ********************************************************
        PROGRESSI:
            -] PROPOSTA DI MODIFICA: Cambiare il nome 'gameId' con 'inviteId'
            -] Aggiunto l'endpoint /replyInvite per gestire le richieste di Accettazione o Rifiuto di inviti.
            -] Aggiunta la classe InviteReplyDTO per rappresentare la richiesta di accettazione/rifiuto di un invito.
               Quando un utente rifiuta l'invito, esso viene cancellato dalla memoria del server

        DA FARE:
            -] Scegliere un numero costante di giocatori che possono comporre una squadra? Oppure dedurlo da quanti
               amici invita l'Inviter? ( -> Più realistico ma è fattibile lato Erlang?)
               Limitare di conseguenza il Rival quando crea la sua squadra.

  ****************************************** ***************************************** *********************************

  ************************************************** 11/03/2024 ********************************************************
    PROGRESSI:
            -] Cambiato il tipo della classe Hashmap in ConcurrentHashMap, perchè la classe HashMap non è ThreadSafe,
               quindi i blocchi synchronized{...} non servono più. -> Cancellare le HashMap non ThreadSafe.
            -] Creata la classe PendingMatch come sostituta delle classi RivalWaiting e TeamCreationWaiting.
               La classe contiene la lista degli utenti che hanno accettato l'invito e che quindi sono in attesa.
            -] Creata la pagina di waiting, in cui viene SEMBRA venga mostrata solo la clessidra, con stile e JS.
            -] Gestita l'attesa degli utenti che accettano l'invito con LATCH all'interno della classe PendingMatch.
               Per adesso il Latch è inizializzato a 4 --> OBBLIGATORO AVERE 2 SQUADRE DA 2!
               La gestione del countdown() viene fatta dai due metodi pubblici.
            -] Modificato l'invio della risposta dell'invito.
               Adesso l'invito (accettato o meno) viene eseguito nella pagina di waiting.

    DA FARE - RIEPILOGO PER GAETANO:
            -] Proseguire con la pagina di gioco vera e propria, quindi quella che si interfaccia con Erlang.
            -] Scegliere un numero costante di giocatori (SEMPLIFICAZIONE) -> 3 -> Controllo in Javascript.
            -] Tutto l'ADMIN
  ****************************************** ***************************************** *********************************
*/





























