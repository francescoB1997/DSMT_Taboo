package it.unipi.dsmt.dsmt_taboo.controller;

import org.springframework.http.ResponseEntity;
import it.unipi.dsmt.dsmt_taboo.model.DTO.LoginRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerReponseDTO;

public interface LoginControllerInterface
{
    public ResponseEntity<ServerReponseDTO> loginRequest(LoginRequestDTO loginRequest);
}
