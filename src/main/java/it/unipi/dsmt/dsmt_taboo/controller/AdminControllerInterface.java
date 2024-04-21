package it.unipi.dsmt.dsmt_taboo.controller;

import it.unipi.dsmt.dsmt_taboo.model.DTO.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface AdminControllerInterface
{
    public ResponseEntity<ServerResponseDTO<Integer>> deleteUser(@RequestBody AdminRequestDTO userToDeleteRequest);

    public ResponseEntity<ServerResponseDTO<List<UserDTO>>> getAllSignedUsers(@RequestBody AdminRequestDTO getAllUserRequest);

    public ResponseEntity<ServerResponseDTO<List<MatchDTO>>> getAllMatches(@RequestBody AdminRequestDTO getAllMatchesRequest);

}
