package it.unipi.dsmt.dsmt_taboo.model.DTO;

public class ServerResponseDTO<T>
{
    /**
     * This class represents a Data Transfer Object (DTO) used for encapsulating server responses in the system.
     * It is a Generic class, allowing different types of response messages to be stored. It includes a field
     * to store the response message and provides methods to retrieve and set the response message.
     */

    private T responseMessage;
    public ServerResponseDTO(T responseMessage) { this.responseMessage = responseMessage; }
    public T getResponseMessage() { return responseMessage; }
    public void setResponseMessage(T responseMessage) { this.responseMessage = responseMessage; }
}
