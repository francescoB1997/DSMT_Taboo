package it.unipi.dsmt.dsmt_taboo.exceptions;

public class DatabaseNotReachableException extends Exception
{
    /**
     * This class represents a checked exception that is thrown when the program is unable to reach the database.
     * It is used to handle situations where communication with the database is not possible due to connection issues
     * or misconfiguration. It extends the Exception class and provides a default message indicating that the database
     * is unreachable.
     */

    public DatabaseNotReachableException()
    {
        super("The DB is not reachable");
    }
}
