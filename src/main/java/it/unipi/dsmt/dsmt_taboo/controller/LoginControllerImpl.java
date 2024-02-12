package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.LoginRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerResponseDTO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.UserDTO;
import it.unipi.dsmt.dsmt_taboo.exceptions.UserNotExistsException;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@SessionAttributes("userlog")     DA INVESTIGARE!!!
public class LoginControllerImpl implements LoginControllerInterface
{
    @Autowired
    SessionManagement session;
    private UserDAO user = new UserDAO();
    @PostMapping("/login")
    @Override
    public ResponseEntity<ServerResponseDTO<String>> loginRequest(@RequestBody  LoginRequestDTO loginRequest)
        // This function handle the login request of a user
    {
        this.session = SessionManagement.getInstance();
        ServerResponseDTO loginResponse;
        HttpStatus responseHttp;
        try
        {
            user.login(loginRequest.getUsername(), loginRequest.getPassword());
            loginResponse = new ServerResponseDTO("LoginOK");
            responseHttp = HttpStatus.OK;
            session.setLogUser(loginRequest.getUsername());
            System.out.println("Utente [" + loginRequest.getUsername() + "] loggato");
        }
        catch (UserNotExistsException e)
        {
            loginResponse = new ServerResponseDTO(e.getMessage());
            responseHttp = HttpStatus.BAD_REQUEST;
            System.out.println("LoginControllerImpl -> " + e.getMessage());
        }
        return new ResponseEntity<>(loginResponse, responseHttp);
    }

    @PostMapping("/logout")
    @Override
    public ResponseEntity<ServerResponseDTO<String>> logoutRequest(@RequestBody String Username)
        // This method is the handler for the logout requests
    {
        System.out.println("Logut request di " + Username);
        ServerResponseDTO logoutResponse;
        HttpStatus responseHttp;
        session = SessionManagement.getInstance();
        if(!session.isUserLogged(Username))
        {
            logoutResponse = new ServerResponseDTO("Logout Failed");
            responseHttp = HttpStatus.FORBIDDEN;
        }
        else
        {
            logoutResponse = new ServerResponseDTO("Logout Success");
            responseHttp = HttpStatus.OK;
        }
        session.logoutUser(Username);
        return new ResponseEntity<>(logoutResponse, responseHttp);
    }

    @PostMapping("/signup")
    @Override
    public ResponseEntity<ServerResponseDTO<String>> signUp(@RequestBody UserDTO userToSignup)
        // This function is responsible for the signup action
    {
        int control = user.signup(userToSignup);
        ServerResponseDTO signupResponse;
        HttpStatus responseHttp;
        if (control == 1)
        {
            session = SessionManagement.getInstance();
            session.setLogUser(userToSignup.getUsername());
            System.out.println("Username: [" + userToSignup.getUsername() + "] completely registered");
            signupResponse = new ServerResponseDTO("Signup Success");
            responseHttp = HttpStatus.OK;
        }
        else if(control == 0)
        {
            signupResponse = new ServerResponseDTO("Username alredy used");
            responseHttp = HttpStatus.BAD_REQUEST;
        }
        else
        {
            signupResponse = new ServerResponseDTO("User not inserted");
            responseHttp = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(signupResponse, responseHttp);
    }
}
