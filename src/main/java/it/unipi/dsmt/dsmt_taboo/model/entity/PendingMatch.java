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
    //Classe che rappresenta un match pendente, ossia non ancora in attesa di risposta di accettazione/rifiuto di invito
{
    private CountDownLatch latch;
    private ArrayList<String> inviterTeamWaiting;
    private ArrayList<String> rivalsTeamWaiting;

    public PendingMatch()
    {
        this.rivalsTeamWaiting = new ArrayList<>();
        this.inviterTeamWaiting = new ArrayList<>();
        latch = new CountDownLatch( Constant.NUMBER_OF_PLAYERS_FOR_MATCH );
    }
    public void addWaitingRival(String usernameRival)
    // This function add a new RivalWaiting only if it does not waiting yet
    {
        if (this.rivalsTeamWaiting.stream().filter(rivalWaiting -> rivalWaiting.equals(usernameRival)).collect(Collectors.toList()).isEmpty())
        {
            this.rivalsTeamWaiting.add(usernameRival);
            this.latch.countDown();
            System.out.println("[R] CountDown: " + this.latch.getCount());
        }
        try { this.latch.await(); }
        catch (Exception e) { System.out.println("Errore eccezione await() [R] -> " + e.getMessage()); }
        //System.out.println("ThreadR di [ " + usernameRival + " ] Sveglio");
    }

    public void addWaitingFriend(String usernameFriend)
    // This function add a new FriendWaiting only if it does not waiting yet
    {
        if (this.inviterTeamWaiting.stream().filter(friendWaiting -> friendWaiting.equals(usernameFriend)).collect(Collectors.toList()).isEmpty())
        {
            this.inviterTeamWaiting.add(usernameFriend);
            this.latch.countDown();
            System.out.println("[F] CountDown: " + this.latch.getCount());
        }
        try { this.latch.await(); }
        catch (Exception e) { System.out.println("Errore eccezione await() [F] -> " + e.getMessage()); }
    }

    public void wakeUpAllThreads()
    {
        long round = this.latch.getCount();
        for(long i = 0; i < round; i++)
            this.latch.countDown();
    }

    public ArrayList<String> getInviterTeamMember() { return this.inviterTeamWaiting; }

    public ArrayList<String> getRivalsTeamMember() { return this.rivalsTeamWaiting; }
}
