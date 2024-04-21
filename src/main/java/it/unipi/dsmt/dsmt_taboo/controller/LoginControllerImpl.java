package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.exceptions.DatabaseNotReachableException;
import it.unipi.dsmt.dsmt_taboo.model.DTO.LoginRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerResponseDTO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.UserDTO;
import it.unipi.dsmt.dsmt_taboo.exceptions.UserNotExistsException;
import it.unipi.dsmt.dsmt_taboo.utility.Constant;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginControllerImpl implements LoginControllerInterface
{
    SessionManagement session;

    @PostMapping("/login") // This function handle the login request of a user
    @Override
    public ResponseEntity<ServerResponseDTO<String>> loginRequest(@RequestBody  LoginRequestDTO loginRequest)
    {
        String usernameRequester = loginRequest.getUsername();
        String passwordRequester = loginRequest.getPassword();
        this.session = SessionManagement.getInstance();
        ServerResponseDTO <String> loginResponse;
        HttpStatus responseHttp;

        UserDAO user = new UserDAO();

        System.out.println("login: Log-in Request From [" + usernameRequester + "]");

        try
        {
            user.login(usernameRequester, passwordRequester);
            if(usernameRequester.equals(Constant.usernameAdmin)
                    && passwordRequester.equals(Constant.passwordAdmin))
                loginResponse = new ServerResponseDTO<>("LoginAdminOK");
            else
                loginResponse = new ServerResponseDTO<>("LoginOK");
            responseHttp = HttpStatus.OK;
            session.setLogUser(usernameRequester);
            System.out.println("login: The User [" + usernameRequester + "] Successfully Logged");
        }
        catch (Exception e)
        {
            if(e instanceof UserNotExistsException)
            {
                System.out.println("login UserNotExistsException: " + e.getMessage());
                responseHttp = HttpStatus.BAD_REQUEST;
            }
            else if (e instanceof DatabaseNotReachableException)
            {
                System.out.println("login DatabaseNotReachableException: " + e.getMessage());
                responseHttp = HttpStatus.BAD_GATEWAY;
            }
            else
            {
                System.out.println("login Ex: " + e.getMessage());
                responseHttp = HttpStatus.BAD_REQUEST;
            }
            loginResponse = new ServerResponseDTO<>(e.getMessage());
        }
        return new ResponseEntity<>(loginResponse, responseHttp);
    }

    @PostMapping("/logout")// This method is the handler for the logout requests
    @Override
    public ResponseEntity<ServerResponseDTO<String>> logoutRequest(@RequestBody String username)
    {
        ServerResponseDTO<String> logoutResponse;
        HttpStatus responseHttp;
        session = SessionManagement.getInstance();

        System.out.println("logout: Logout Request From [" + username + "]");

        if(!session.isUserLogged(username))
        {
            logoutResponse = new ServerResponseDTO<>("Logout Failed");
            responseHttp = HttpStatus.UNAUTHORIZED;
        }
        else
        {
            logoutResponse = new ServerResponseDTO<>("Logout Success");
            responseHttp = HttpStatus.OK;
        }
        session.logoutUser(username);

        System.out.println("logout: The User [" + username + "] Successfully Exited The System");

        return new ResponseEntity<>(logoutResponse, responseHttp);
    }

    @PostMapping("/signup") // This function is responsible for the signup action request
    @Override
    public ResponseEntity<ServerResponseDTO<String>> signUp(@RequestBody UserDTO userToSignup)
    {
        UserDAO user = new UserDAO();
        System.out.println("signup: Sign-up Request From [" + userToSignup.getUsername() + "]");
        int control;

        // To avoid conflict with admin credentials
        if(userToSignup.getUsername().contains(Constant.usernameAdmin)
            || userToSignup.getUsername().equals(Constant.usernameAdmin))
            control = -1;
        else
            control = user.signup(userToSignup);

        ServerResponseDTO<String> signupResponse;
        HttpStatus responseHttp;
        if (control == 1)
        {
            System.out.println("signup: [" + userToSignup.getUsername() + "] has completed the Registration");
            signupResponse = new ServerResponseDTO<>("Signup Success");
            responseHttp = HttpStatus.OK;
        }
        else if(control == 0)
        {
            System.out.println("signup: The username: [" + userToSignup.getUsername() + "] already exists");
            signupResponse = new ServerResponseDTO<>("Username already used");
            responseHttp = HttpStatus.BAD_REQUEST;
        }
        else
        {
            signupResponse = new ServerResponseDTO<>("User not inserted");
            responseHttp = HttpStatus.BAD_GATEWAY;
        }
        return new ResponseEntity<>(signupResponse, responseHttp);
    }
}
