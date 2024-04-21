package it.unipi.dsmt.dsmt_taboo.utility;

import org.springframework.stereotype.Component;
import java.util.ArrayList;

@Component
public class SessionManagement
{
    /**
     * This class represents a session management utility for tracking logged-in users.
     * It is annotated with @Component to indicate that it is a Spring component.
     * It includes a static method getInstance() to retrieve the singleton instance of the SessionManagement class.
     * The class maintains an ArrayList of usernames representing users currently logged in.
     * Methods are provided to set a user as logged in, check if a user is logged in, and log out a user.
     * The setLogUser method adds a username to the list of logged-in users.
     * The isUserLogged method checks if a given username is present in the list of logged-in users.
     * The logoutUser method removes a user from the list of logged-in users upon logout.
     */

    private static SessionManagement session = null;
    private ArrayList<String> userLogged = new ArrayList<>();

    private SessionManagement() {}

    public static SessionManagement getInstance()
    {
        if(session == null)
            session = new SessionManagement();
        return session;
    }

    public void setLogUser(String username)
    {
        if(session == null)
            throw new RuntimeException("Session is not active.");
        else
            userLogged.add(username);
    }

    public boolean isUserLogged(String Username)
    {
        if(session == null)
            throw new RuntimeException("Session is not active.");
        else
            return userLogged.contains(Username);
    }

    public boolean logoutUser(String Username)
    {
        if(isUserLogged(Username))
        {
            userLogged.remove(Username);
            return true;
        }
        return false;
    }
}
