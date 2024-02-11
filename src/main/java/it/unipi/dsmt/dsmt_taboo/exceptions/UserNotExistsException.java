package it.unipi.dsmt.dsmt_taboo.exceptions;

public class UserNotExistsException extends Exception
{
    public UserNotExistsException ()
    {
        super("Login Error. Please, check the Username or Password");
    }
}
