package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class ServerReponseDTO
{
    private String responseMessage;

    public ServerReponseDTO(String responseMessage) { this.responseMessage = responseMessage; }

    public String getResponseMessage() { return responseMessage; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }
}
