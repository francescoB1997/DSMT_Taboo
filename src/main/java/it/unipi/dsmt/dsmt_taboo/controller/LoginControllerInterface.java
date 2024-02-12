package it.unipi.dsmt.dsmt_taboo.controller;

import org.springframework.http.ResponseEntity;
import it.unipi.dsmt.dsmt_taboo.model.DTO.LoginRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerReponseDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.UserDTO;
import org.springframework.web.bind.annotation.RequestBody;

public interface LoginControllerInterface
{
    public ResponseEntity<ServerReponseDTO> loginRequest(LoginRequestDTO loginRequest);
    public ResponseEntity<String> logoutRequest(String logoutRequest);
    public ResponseEntity<String> signUp(@RequestBody UserDTO UserSignUp);
}
