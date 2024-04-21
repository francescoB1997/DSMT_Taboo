package it.unipi.dsmt.dsmt_taboo.controller;

import org.springframework.http.ResponseEntity;
import it.unipi.dsmt.dsmt_taboo.model.DTO.LoginRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerResponseDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.UserDTO;
import org.springframework.web.bind.annotation.RequestBody;

public interface LoginControllerInterface
{
    public ResponseEntity<ServerResponseDTO<String>> loginRequest(LoginRequestDTO loginRequest);
    public ResponseEntity<ServerResponseDTO<String>> logoutRequest(String logoutRequest);
    public ResponseEntity<ServerResponseDTO<String>> signUp(@RequestBody UserDTO UserSignUp);
}
