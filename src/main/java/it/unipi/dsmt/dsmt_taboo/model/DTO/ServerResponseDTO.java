package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class ServerResponseDTO<T>
{
    private T responseMessage;
    public ServerResponseDTO(T responseMessage) { this.responseMessage = responseMessage; }
    public T getResponseMessage() { return responseMessage; }
    public void setResponseMessage(T responseMessage) { this.responseMessage = responseMessage; }
}
