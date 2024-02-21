package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.DAO.FriendDAO;
import it.unipi.dsmt.dsmt_taboo.exceptions.UserNotExistsException;
import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LoggedUserControllerImpl implements LoggedUserControllerInterface
    // This class handle the action performed by a Logged User
{
    @Autowired
    SessionManagement session;

    @PostMapping("/getFriendList")
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

        //---ServerResponseDTO getFriendListResponse;---
        // L'uso della classe ServerResponseDTO come generic (per via di <T> e quindi parametrizzata),
        // richiede un uso differente all'interno di questo metodo.
        //
        // Più precisamente dato che la classe ServerResponseDTO è generica e quindi richiede "parametri
        // di tipo" per essere istanziata, bisogna "specificare tali parametri di tipo nel punto (istante)
        // in cui viene creato il nuovo oggetto" (uso corretto), altrimenti vengono fuori dei warining del tipo:
        //      "Raw use of parameterized class 'ServerResponseDTO'"
        // che indicano un uso della classe in modo "grezzo" o non specificato; nel senso che non si
        // sta specificando il tipo di dati che contiene, come invece ci si aspetterebbe quando si fa
        // uso di classi parametrizzate.
        // Un altro inghippo logico e lecito è che si potrebbe appurare che si sta facendo
        // uso di questa classe come se fosse generica, quando in realtà non lo è (ma di fatto lo è).
        // Tutto questo per dire che anche un warning, su questa tematica, potrebbe attirare 'attenzione
        // del prof riguardo alla tematica di fare mix di elementi generici e non generici o di uso delle
        // generics in java generale; avendo trattato l'ragomento durante il corso.
    }

    @PostMapping("/removeFriend")
    @Override
    public ResponseEntity<ServerResponseDTO<String>>
    removeFriendRequest(@RequestBody FriendRequestDTO removeRequest)
    {
        ServerResponseDTO <String> removeFriendResponse;
        HttpStatus responseHttp;

        FriendDAO friendDAO = new FriendDAO(removeRequest.getUsername());

         if(friendDAO.removeFriend(removeRequest.getUsername(), removeRequest.getUsernameFriend())) {
             removeFriendResponse = new ServerResponseDTO<>(
                     removeRequest.getUsernameFriend() + " removed from your Friend List");
             responseHttp = HttpStatus.OK;
         }
         else {
             removeFriendResponse = new ServerResponseDTO<>(
                     "Error occurred, friend not removed");
             responseHttp = HttpStatus.BAD_REQUEST;
         }

        return new ResponseEntity<>(removeFriendResponse, responseHttp);
    }
}
