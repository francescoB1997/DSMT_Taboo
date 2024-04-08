package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.DAO.FriendDAO;
import it.unipi.dsmt.dsmt_taboo.DAO.MatchDAO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import it.unipi.dsmt.dsmt_taboo.utility.Constant;
import it.unipi.dsmt.dsmt_taboo.utility.SessionManagement;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AdminControllerImpl implements AdminControllerInterface
{
    @PostMapping("/deleteUser")
    @Override
    public ResponseEntity<ServerResponseDTO<Integer>> deleteUser(@RequestBody AdminRequestDTO userToDeleteRequest)
    {
        HttpStatus responseHttp;
        ServerResponseDTO<Integer> userToDeleteResponse;
        boolean checkLogin = SessionManagement.getInstance().
                isUserLogged(userToDeleteRequest.getUsername());
        int requestStatus = 0;

        if(checkLogin)
        {
            if(!checkAdmin(userToDeleteRequest)) {
                responseHttp = HttpStatus.UNAUTHORIZED;
            } else {

                UserDAO userDAO = new UserDAO();
                boolean removeOK = userDAO.removeUser(userToDeleteRequest.getParameter());
                if(removeOK)
                {
                    System.out.println("\nThe user "
                            + userToDeleteRequest.getParameter() +
                            " has been successfully removed\n");
                    responseHttp = HttpStatus.OK;
                    userToDeleteResponse = new ServerResponseDTO<>(requestStatus);
                }
                else
                {
                    System.out.println("\nError occurred during remove operation." +
                            userToDeleteRequest.getParameter() +
                            " has NOT been removed from your friend list\n");
                    requestStatus++;
                    responseHttp = HttpStatus.BAD_REQUEST;
                    userToDeleteResponse = new ServerResponseDTO<>(requestStatus);
                }
                return new ResponseEntity<>(userToDeleteResponse, responseHttp);
            }

            userToDeleteResponse = new ServerResponseDTO<>(null);
            return new ResponseEntity<>(userToDeleteResponse, responseHttp);

        } else {

            System.out.println("\nAdminController: getUsers request from a NonLogged user\n");
            userToDeleteResponse = new ServerResponseDTO<>(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(userToDeleteResponse, responseHttp);
    }

    @PostMapping("/getUsers")
    @Override
    public ResponseEntity<ServerResponseDTO<List<UserDTO>>>
    getAllSignedUsers(@RequestBody AdminRequestDTO getUserRequest)
    {
        HttpStatus responseHttp;
        ServerResponseDTO<List<UserDTO>> getUserResponse = null;
        boolean checkLogin = SessionManagement.getInstance().
                isUserLogged(getUserRequest.getUsername());

        if(checkLogin)
        {
            if(!checkAdmin(getUserRequest)) {
                responseHttp = HttpStatus.UNAUTHORIZED;
            } else {

                UserDAO userDAO = new UserDAO();
                List<UserDTO> userList = userDAO.globalSearchUser(getUserRequest.getParameter());

                if(userList.isEmpty())
                {
                    if(!getUserRequest.getParameter().equals("")) {
                        System.out.println("\nAdminController: - NOT FOUND - Database NOT contains the user: "
                                           + getUserRequest.getParameter() + "\n");
                    }
                    getUserResponse = new ServerResponseDTO<>(null);

                } else {

                    if(!getUserRequest.getParameter().equals("")) {
                        System.out.println("\nAdminController: - OK - Database contains the user: "
                                           + getUserRequest.getParameter() + "\n");
                    }
                    getUserResponse = new ServerResponseDTO<>(userList);
                }

                responseHttp = HttpStatus.OK;
                return new ResponseEntity<>(getUserResponse, responseHttp);
            }

            return new ResponseEntity<>(getUserResponse, responseHttp);

        } else {
            System.out.println("\nAdminController: getUsers request from a NonLogged user\n");
            getUserResponse = new ServerResponseDTO<>(null);
            responseHttp = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(getUserResponse, responseHttp);
    }

    @PostMapping("/getAllMatches")
    @Override
    public ResponseEntity<ServerResponseDTO<List<MatchDTO>>> getAllMatches(@RequestBody AdminRequestDTO getAllMatchesRequest) {

        HttpStatus responseHttp;
        ServerResponseDTO<List<MatchDTO>> getAllMatchesResponse = null;

        if(!checkAdmin(getAllMatchesRequest))
            responseHttp = HttpStatus.UNAUTHORIZED;
        else
        {
            MatchDAO matchDAO = new MatchDAO();
            getAllMatchesResponse = new ServerResponseDTO<>(matchDAO.getMatches(""));
            responseHttp = HttpStatus.OK;
        }
        return new ResponseEntity<>(getAllMatchesResponse, responseHttp);
    }

    private Boolean checkAdmin(AdminRequestDTO request)
    {
        return Constant.passwordAdmin.equals(request.getPassword());
    }
}
