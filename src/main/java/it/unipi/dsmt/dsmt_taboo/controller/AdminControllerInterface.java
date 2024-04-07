package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface AdminControllerInterface
{
    // delete user
    public ResponseEntity<ServerResponseDTO<Boolean>> deleteUser(AdminRequestDTO userToDeleteRequest);

    // get all signed user
    public ResponseEntity<ServerResponseDTO<List<UserDTO>>> getAllSignedUsers(AdminRequestDTO getAllUserRequest);

    // get all online user ?


    // search all matches
    public ResponseEntity<ServerResponseDTO<List<MatchDTO>>> getAllMatches(AdminRequestDTO getAllMatchesRequest);

}
