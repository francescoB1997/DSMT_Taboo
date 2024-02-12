package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.LoginRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerReponseDTO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.UserDTO;
import it.unipi.dsmt.dsmt_taboo.exceptions.UserNotExistsException;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;
import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import java.util.logging.Logger;

@RestController
//@SessionAttributes("userlog")     DA INVESTIGARE!!!
public class LoginControllerImpl implements LoginControllerInterface
{
    @Autowired
    SessionManagement session;
    private UserDAO user = new UserDAO();
    @PostMapping("/login")
    @Override
    public ResponseEntity<ServerReponseDTO> loginRequest(@RequestBody  LoginRequestDTO loginRequest)
    {
        this.session = SessionManagement.getInstance();
        ServerReponseDTO loginResponse;
        HttpStatus responseHttp;
        try
        {
            user.login(loginRequest.getUsername(), loginRequest.getPassword());
            loginResponse = new ServerReponseDTO("LoginOK");
            responseHttp = HttpStatus.OK;
            session.setLogUser(loginRequest.getUsername());
            System.out.println("Utente [" + loginRequest.getUsername() + "] loggato");
        }
        catch (UserNotExistsException e)
        {
            loginResponse = new ServerReponseDTO(e.getMessage());
            responseHttp = HttpStatus.BAD_REQUEST;
            System.out.println("LoginControllerImpl -> " + e.getMessage());
        }
        return new ResponseEntity<>(loginResponse, responseHttp);
    }

    @PostMapping("/logout")
    @Override
    public ResponseEntity<ServerReponseDTO> logoutRequest(@RequestBody String Username)
    {
        System.out.println("Logut request di " + Username);
        ServerReponseDTO logoutResponse;
        HttpStatus responseHttp;
        session = SessionManagement.getInstance();
        if(!session.isUserLogged(Username))
        {
            logoutResponse = new ServerReponseDTO("Logout Failed");
            responseHttp = HttpStatus.FORBIDDEN;
        }
        else
        {
            logoutResponse = new ServerReponseDTO("Logout Success");
            responseHttp = HttpStatus.OK;
        }
        session.logoutUser(Username);
        return new ResponseEntity<>(logoutResponse, responseHttp);
    }

    @PostMapping("/signup")
    @Override
    public ResponseEntity<ServerReponseDTO> signUp(@RequestBody UserDTO userToSignup)
    {
        int control = user.signup(userToSignup);
        ServerReponseDTO signupResponse;
        HttpStatus responseHttp;
        if (control == 1)
        {
            session = SessionManagement.getInstance();
            session.setLogUser(userToSignup.getUsername());
            System.out.println("Username: [" + userToSignup.getUsername() + "] completely registered");
            signupResponse = new ServerReponseDTO("Signup Success");
            responseHttp = HttpStatus.OK;
        }
        else if(control == 0)
        {
            signupResponse = new ServerReponseDTO("Username alredy used");
            responseHttp = HttpStatus.BAD_REQUEST;
        }
        else
        {
            signupResponse = new ServerReponseDTO("User not inserted");
            responseHttp = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(signupResponse, responseHttp);
    }
}
