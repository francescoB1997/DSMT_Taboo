package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.DAO.MatchDAO;
import it.unipi.dsmt.dsmt_taboo.DAO.UserDAO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.AdminRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.MatchDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerResponseDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.UserDTO;
import it.unipi.dsmt.dsmt_taboo.utility.Constant;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

public class AdminControllerImpl implements AdminControllerInterface
{
    @PostMapping("/deleteUser")
    @Override
    public ResponseEntity<ServerResponseDTO<Boolean>> deleteUser(AdminRequestDTO userToDeleteRequest)
    {
        HttpStatus responseHttp;
        ServerResponseDTO<Boolean> userToDeleteResponse = null;

        if(!checkAdmin(userToDeleteRequest))
            responseHttp = HttpStatus.UNAUTHORIZED;
        else
        {
            UserDAO userDAO = new UserDAO();
            Boolean removeOK = userDAO.removeUser(userToDeleteRequest.getParameter());
            if(removeOK)
            {
                responseHttp = HttpStatus.OK;
                userToDeleteResponse = new ServerResponseDTO<>(true);
            }
            else
            {
                responseHttp = HttpStatus.BAD_REQUEST;
                userToDeleteResponse = new ServerResponseDTO<>(false);
            }
        }
        return new ResponseEntity<>(userToDeleteResponse, responseHttp);
    }

    @PostMapping("/getAllUsers")
    @Override
    public ResponseEntity<ServerResponseDTO<List<UserDTO>>> getAllSignedUsers(AdminRequestDTO getAllUserRequest)
    {
        HttpStatus responseHttp;
        ServerResponseDTO<List<UserDTO>> getAllUserResponse = null;

        if(!checkAdmin(getAllUserRequest))
            responseHttp = HttpStatus.UNAUTHORIZED;
        else
        {
            UserDAO userDAO = new UserDAO();
            getAllUserResponse = new ServerResponseDTO<>( userDAO.globalSearchUser("")); // if you pass the EmptyString, the globalUserSearch returns all users
            responseHttp = HttpStatus.OK;
        }
        return new ResponseEntity<>(getAllUserResponse, responseHttp);
    }

    @PostMapping("/getAllMatches")
    @Override
    public ResponseEntity<ServerResponseDTO<List<MatchDTO>>> getAllMatches(AdminRequestDTO getAllMatchesRequest) {

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
