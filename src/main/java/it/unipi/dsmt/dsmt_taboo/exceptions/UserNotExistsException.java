package it.unipi.dsmt.dsmt_taboo.exceptions;

public class UserNotExistsException extends Exception
{
    /**
     * This class represents a checked exception that is thrown when a user does not exist in the system
     * during a login attempt. It extends the Exception class and provides a default message indicating
     * a login error due to incorrect username or password. This exception is used to handle situations
     * where the user credentials provided during login do not match any existing user records in the system.
     */

    public UserNotExistsException ()
    {
        super("Login Error. Please, check the Username or Password");
    }
}
