package it.unipi.dsmt.dsmt_taboo.model.entity;

import it.unipi.dsmt.dsmt_taboo.utility.Constant;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/*
    ATTENZIONE, un utente per poter aggiungersi in uno dei due team DEVE bloccarsi all'interno del latch.
    In questo modo verrà creato un pendingMatch consistente con l'invito (se tutti accettano).
 */
public class PendingMatch
{

    /**
     * This class represents a pending match, which is a match that is not yet awaiting responses
     * for invitation acceptance or rejection. It includes fields for a CountDownLatch object,
     * which is used to synchronize the invitation process, and lists of usernames representing members
     * of the inviting team and rival team waiting for responses. The class provides methods to add users
     * to the waiting lists for both teams, ensuring that each user is added only once and that
     * the CountDownLatch is appropriately decremented. Additionally, it includes a method to wake up all threads
     * awaiting the completion of the invitation process. The class facilitates the management of
     * pending match invitations within the application.
     */

    private CountDownLatch latch;
    private ArrayList<String> inviterTeamWaiting;
    private ArrayList<String> rivalsTeamWaiting;

    /**
     * Constructs a new PendingMatch object with an initialized CountDownLatch
     * and empty waiting lists for both teams.
     */
    public PendingMatch()
    {
        this.rivalsTeamWaiting = new ArrayList<>();
        this.inviterTeamWaiting = new ArrayList<>();
        latch = new CountDownLatch( Constant.NUMBER_OF_PLAYERS_FOR_MATCH );
    }

    /**
     * Adds a new rival waiting for the match invitation, ensuring it is added only once.
     * Decrements the CountDownLatch and awaits completion of the invitation process.
     * @param usernameRival The username of the rival waiting for the invitation.
     */
    public void addWaitingRival(String usernameRival)
    {
        if (this.rivalsTeamWaiting.stream().filter(rivalWaiting ->
                                            rivalWaiting.equals(usernameRival)).
                                            collect(Collectors.toList()).isEmpty())
        {
            this.rivalsTeamWaiting.add(usernameRival);
            this.latch.countDown();
            System.out.println("[R] CountDown: " + this.latch.getCount());
        }
        try { this.latch.await(); }
        catch (Exception e) { System.out.println("Error: await() exception [R] -> " + e.getMessage()); }
    }

    /**
     * Adds a new friend waiting for the match invitation, ensuring it is added only once.
     * Decrements the CountDownLatch and awaits completion of the invitation process.
     * @param usernameFriend The username of the friend waiting for the invitation.
     */
    public void addWaitingFriend(String usernameFriend)
    {
        if (this.inviterTeamWaiting.stream().filter(friendWaiting ->
                                            friendWaiting.equals(usernameFriend)).
                                            collect(Collectors.toList()).isEmpty())
        {
            this.inviterTeamWaiting.add(usernameFriend);
            this.latch.countDown();
            System.out.println("[F] CountDown: " + this.latch.getCount());
        }
        try { this.latch.await(); }
        catch (Exception e) { System.out.println("Error: await() exception [F] -> " + e.getMessage()); }
    }

    /**
     * Wakes up all threads awaiting the completion of the invitation process by decrementing the CountDownLatch.
     */
    public void wakeUpAllThreads()
    {
        long round = this.latch.getCount();
        for(long i = 0; i < round; i++)
            this.latch.countDown();
    }

    /**
     * Retrieves the list of usernames representing members of the inviting team waiting for responses.
     * @return The list of usernames of members of the inviting team waiting for responses.
     */
    public ArrayList<String> getInviterTeamMember() { return this.inviterTeamWaiting; }

    /**
     * Retrieves the list of usernames representing members of the rival team waiting for responses.
     * @return The list of usernames of members of the rival team waiting for responses.
     */
    public ArrayList<String> getRivalsTeamMember() { return this.rivalsTeamWaiting; }
}
