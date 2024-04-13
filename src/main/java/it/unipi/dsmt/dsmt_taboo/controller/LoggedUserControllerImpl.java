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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

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
        if (SessionManagement.getInstance().isUserLogged(username))  //Check if that user is logged
        {
            FriendDAO friendDAO = new FriendDAO(username);
            getFriendListResponse = new ServerResponseDTO<>(friendDAO.getFriendList());
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
    // The server response is a JSON message that contains the list of user, checking before that the requesterUser is 'OK'.
    {
        System.out.println("\nLoggedUserController: searchUser request from " +
                "[" + userSearchRequestDTO.getRequesterUsername() + "] -> " +
                "Searching [" + userSearchRequestDTO.getUsernameToSearch() + "]" + "\n");

        HttpStatus responseHttp;
        ServerResponseDTO<List<UserDTO>> userListResponse;
        boolean checkLogin = SessionManagement.getInstance().
                isUserLogged(userSearchRequestDTO.getRequesterUsername());

        if (checkLogin) //Check if requesterUsername is logged or Not.
        {
            UserDAO userDAO = new UserDAO();
            List<UserDTO> userList = userDAO.globalSearchUser(userSearchRequestDTO.
                    getUsernameToSearch());
            if (userList.isEmpty()) {
                System.out.println("\nLoggedUserController: - NOT FOUND - Database NOT contains the user: "
                        + userSearchRequestDTO.getUsernameToSearch() + "\n");
                userListResponse = new ServerResponseDTO<>(null);

            } else {
                System.out.println("\nLoggedUserController: - OK - Database contains the user: "
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
    removeFriend(@RequestBody FriendshipRequestDTO requesterUsername) {
        System.out.println("\nLoggedUserController: removeUser request from " +
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
            if (removeOpStatus) {
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
    public ResponseEntity<ServerResponseDTO<Integer>> addFriend(@RequestBody FriendshipRequestDTO addFriendRequest) {
        ServerResponseDTO<Integer> addFriendResponse;
        HttpStatus responseHttp;
        boolean checkLogin = SessionManagement.getInstance().isUserLogged(addFriendRequest.getUsername());
        if (checkLogin) {
            FriendDAO me = new FriendDAO(addFriendRequest.getUsername());
            int friendRequestStatus = me.addFriend(addFriendRequest.getUsernameFriend());
            addFriendResponse = new ServerResponseDTO<>(friendRequestStatus);
            if (friendRequestStatus >= 0)
                responseHttp = HttpStatus.OK;
            else
                responseHttp = HttpStatus.BAD_REQUEST;
        } else {
            System.out.println("\nLoggedUserController: addFriend request from a NonLogged user\n");
            addFriendResponse = new ServerResponseDTO<>(-2);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(addFriendResponse, responseHttp);
    }

    Vector<InviteFriends> invites = new Vector<>();
    final ConcurrentHashMap<String, PendingMatch> pendingMatchMap = new ConcurrentHashMap<>();
    final ConcurrentHashMap<String, MatchDTO> runningMatch = new ConcurrentHashMap<>();

    @Async
    @PostMapping("/inviteFriends")
    @Override
    public ResponseEntity<ServerResponseDTO<String>> inviteFriends(@RequestBody InviteFriendRequestDTO request) {
        ServerResponseDTO<String> responseMessage = null;
        HttpStatus responseHttp;
        boolean checkLogin = SessionManagement.getInstance().isUserLogged(request.getUserInviter());
        if (checkLogin) {
            if (request.getGameId().isEmpty()) // If it is first time that i receive that invite...
            {
                request.setAutoGameId();
                invites.add(new InviteFriends(request));

                PendingMatch myPendingMatch = new PendingMatch();
                pendingMatchMap.put(request.getGameId(), myPendingMatch);

                responseMessage = new ServerResponseDTO<>(request.getGameId());
            } else // Else, if the gameId is already setted, then it means the Rival sent this POST
            {
                System.out.print("<R> ");
                responseMessage = new ServerResponseDTO<>(request.getGameId());
                invites.removeIf((invite -> invite.getGameId().equals(request.getGameId()))); // remove the incomplete invite
                invites.forEach(invite ->
                {
                    assert (!invite.getGameId().equals(request.getGameId())); // DBG. Se va storto, la remove ha funzionato!
                });
                invites.add(new InviteFriends(request)); //add the complete invite (with rivals list and rivalsRoles)
                //InviteFriends X = invites.stream().filter(inviteFriends -> inviteFriends.getGameId() == request.getGameId()).toList().get(0);
                //X.printInfoInvite();
                //request.printInfoInvite();
            }
            responseHttp = HttpStatus.OK;
        } else {
            System.out.println("\nLoggedUserController: addFriend request from a NonLogged user\n");
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(responseMessage, responseHttp);
    }

    @Async
    @PostMapping("/checkInvite")
    @Override
    public ResponseEntity<ServerResponseDTO<InviteFriends>> checkInvite(@RequestBody String usernameRequester) {
        ServerResponseDTO<InviteFriends> receivedInvite = null;
        HttpStatus httpStatus = HttpStatus.OK;
        Boolean checkLogin = SessionManagement.getInstance().isUserLogged(usernameRequester);
        if (checkLogin) {
            for (InviteFriends invite : invites) // Search for any invite (inTeam or Rival)
            {
                for (String usernameRival : invite.getRivals()) {
                    if (usernameRequester.equals(usernameRival)) {
                        receivedInvite = new ServerResponseDTO<>(invite);
                        //System.out.println("True in if RIVAL");
                        break;
                    }
                }

                for (String usernameInTeam : invite.getYourTeam()) {
                    if (usernameInTeam.equals(usernameRequester)) {
                        receivedInvite = new ServerResponseDTO<>(invite);
                        break;
                    }
                }
            }
        } else
            httpStatus = HttpStatus.UNAUTHORIZED;

        // -------------------- ONLY FOR DEBUG --------------------
        if ((receivedInvite != null) && (httpStatus == HttpStatus.OK)) {
            System.out.println("Invite found for [" + usernameRequester + "] received by [" +
                    receivedInvite.getResponseMessage().getUserInviter() + "]");
        } else if (httpStatus == HttpStatus.UNAUTHORIZED)
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
        InviteFriends r = invites.stream().filter(invite -> invite.getGameId().equals(replyInvite.getGameId())).toList().get(0);

        if (!replyInvite.getInviteState()) // If the invite has been refused...
        {
            System.out.println("replyInvite:  [" + replyInvite.getSenderUsername() + "] ha rifiutato l'invito di [" + r.getUserInviter() + "]");
            //invites.removeIf(invite -> invite.getGameId().equals(replyInvite.getGameId()));
            invites.remove(r);
            // -------------------- ONLY FOR DEBUG --------------------
            invites.forEach(invite ->
            {
                assert (!invite.getGameId().equals(replyInvite.getGameId())); // DBG. Se va storto, la remove non funziona!
            });
            // ----------------------------------------------------------
            pendingMatchMap.get(replyInvite.getGameId()).wakeUpAllThreads();
            //response = new ServerResponseDTO<>(0); // The 0, means that someone have refused the invite
            pendingMatchMap.remove(replyInvite.getGameId()); // Free the memory for the old pendingMatch
        } else {
            System.out.println("replyInvite:  [" + replyInvite.getSenderUsername() + "] ha accettato l'invito di [" + r.getUserInviter() + "]");

            // Metto in attesa il replySender
            if (replyInvite.getInvitedAsFriend())
                pendingMatchMap.get(replyInvite.getGameId()).addWaitingFriend(replyInvite.getSenderUsername());
            else
                pendingMatchMap.get(replyInvite.getGameId()).addWaitingRival(replyInvite.getSenderUsername());

            // l'aggiornamento di r è NECESSARIO, perchè altrimenti gli users che erano entrati in attesa prima che
            // il rivale costruisse la sua squadra, manterrebero il riferimento all'invito INCOMPLETO.
            r = invites.stream().filter(invite -> invite.getGameId().equals(replyInvite.getGameId())).toList().get(0);
            PendingMatch pendingMatch = pendingMatchMap.get(replyInvite.getGameId()); // retrieve the PendingMatch related to this reply

            if (pendingMatch != null && r != null) {
                MatchDTO matchDTO = new MatchDTO(replyInvite.getGameId(),
                        pendingMatch.getInviterTeamMember(), r.getRoles(),
                        pendingMatch.getRivalsTeamMember(), r.getRivalsRoles());

                MatchDTO returned = runningMatch.putIfAbsent(replyInvite.getGameId(), matchDTO); // Il returned è solo per DGB
                /*
                // -------------------- ONLY FOR DEBUG --------------------
                System.out.println("matchDTO = { InviterTeam = [ " + matchDTO.getInviterTeam() + " ]\n" +
                        "InviterTeamRoles=" + matchDTO.getRolesInviterTeam() + "]\n" +
                        "RivalTeam = [ " + matchDTO.getRivalTeam() + " ]\n" +
                        "RivalTeamRoles= [ " + matchDTO.getRolesRivalTeam() + " ]}");
                if(returned != null)
                    System.out.println("Thread: esisteva già il MatchDTO nei runningMatch");
                else
                    System.out.println("THread: *** questo messaggio dovrei vederla una volta ***");
                // ----------------------------------------------------------

                 */
                response = new ServerResponseDTO<>(matchDTO); // The 1, means that all of users have accepted the invite
            } else // The else means that, someone of the invited user has refused the invite"
            {
                if (pendingMatch == null)
                    System.out.println("Invito rifiutato");
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/getMyMatches")
    @Override
    public ResponseEntity<ServerResponseDTO<List<MatchDTO>>> getMyMatches(@RequestBody String usernameRequester) {
        HttpStatus responseHttp;
        ServerResponseDTO<List<MatchDTO>> getAllMatchesResponse = null;
        boolean checkLogin = SessionManagement.getInstance().isUserLogged(usernameRequester);
        if (checkLogin) {
            MatchDAO matchDAO = new MatchDAO();
            getAllMatchesResponse = new ServerResponseDTO<>(matchDAO.getMatches(usernameRequester));
            responseHttp = HttpStatus.OK;
        } else
            responseHttp = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(getAllMatchesResponse, responseHttp);
    }

    @PostMapping("/addNewMatch")
    @Override
    public ResponseEntity<ServerResponseDTO<Integer>> addNewMatch(@RequestBody MatchResultRequestDTO userMatchResult) {
        System.out.println("AddMatch: " + userMatchResult.getMatchId() + "PunteggioInv" + userMatchResult.getScoreInviterTeam() + " | PunteggioRiv"
                + userMatchResult.getScoreRivalTeam());
        HttpStatus responseHttp = HttpStatus.OK;
        ServerResponseDTO<Integer> addMatchResponse = new ServerResponseDTO<>(1);

        List<InviteFriends> r = invites.stream().filter(invite -> invite.getGameId().equals(userMatchResult.getMatchId())).toList();
        if (r != null && !r.isEmpty()) {
            InviteFriends inviteToRemove = r.get(0);
            invites.remove(inviteToRemove);
        }

        MatchDTO matchInfo = runningMatch.get(userMatchResult.getMatchId());
        if (matchInfo != null) {
            // ***** Io thread, posso non bloccarmi ? Ossia l'altro thread è bloccato nel Lock del pendingResultMatch?
            if (userMatchResult.getScoreInviterTeam() != null) {
                System.out.println("Sono il prompter dell'inviter. Abbiamo fatto: " + userMatchResult.getScoreInviterTeam());
                matchInfo.setScoreInviterTeam(userMatchResult.getScoreInviterTeam());
            } else {
                System.out.println("Sono il prompter del rival. Abbiamo fatto: " + userMatchResult.getScoreRivalTeam());
                matchInfo.setScoreRivalTeam(userMatchResult.getScoreRivalTeam());
            }

            matchInfo = runningMatch.get(userMatchResult.getMatchId());

            if (matchInfo.getScoreRivalTeam() != null && matchInfo.getScoreInviterTeam() != null) // questo if dovrebbe essere inutile
            {
                System.out.println("Server: Informazione completa -> I[" + matchInfo.getScoreInviterTeam() + "] R[" + matchInfo.getScoreRivalTeam() + "]");
                MatchDAO matchDAO = new MatchDAO();
                boolean addOpStatus = matchDAO.addNewMatch(matchInfo);

                if (addOpStatus) {
                    //System.out.println("\nThe match has been successfully added into DB\n");
                    //addMatchResponse = new ServerResponseDTO<>(1);
                    runningMatch.remove(userMatchResult.getMatchId());
                } else {
                    System.out.println("\nError occurred during adding opration:" +
                            "The match has NOT been added into DB\n");
                    addMatchResponse = new ServerResponseDTO<>(-1);
                    responseHttp = HttpStatus.BAD_REQUEST;
                }

            } else
                System.out.println("Non puoi svegliarti con ancora uno a NULL");
        } else {
            responseHttp = HttpStatus.BAD_REQUEST;
            addMatchResponse = new ServerResponseDTO<>(-2);
        }
        return new ResponseEntity<>(addMatchResponse, responseHttp);
    }


    @PostMapping("/getMatchResult")
    @Override
    public ResponseEntity<ServerResponseDTO<MatchResultRequestDTO>> getMatchResult(@RequestBody MatchResultRequestDTO matchResultRequestDTO)
    {


        return null;
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





























