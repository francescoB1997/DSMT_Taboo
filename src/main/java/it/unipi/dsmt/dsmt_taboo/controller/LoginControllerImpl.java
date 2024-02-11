package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.LoginRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerReponseDTO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
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
@SessionAttributes("userlog")
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
        }
        catch (UserNotExistsException e)
        {
            loginResponse = new ServerReponseDTO(e.getMessage());
            responseHttp = HttpStatus.BAD_REQUEST;
            System.out.println("LoginControllerImpl -> UserNotExistsException: " + e.getMessage());

        }

        return new ResponseEntity<>(loginResponse, responseHttp);
    }
}
