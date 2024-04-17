package it.unipi.dsmt.dsmt_taboo.exceptions;

public class DatabaseNotReachableException extends Exception
{
    public DatabaseNotReachableException()
    {
        super("The DB is not reachable");
    }
}
