package it.unipi.dsmt.dsmt_taboo.model.entity;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class PendingMatch
{
    private CountDownLatch latch;
    private ArrayList<String> inviterTeamWaiting;
    private ArrayList<String> rivalsTeamWaiting;
    private Boolean refusedInvite;

    public PendingMatch()
    {
        this.rivalsTeamWaiting = new ArrayList<>();
        this.inviterTeamWaiting = new ArrayList<>();
        System.out.println("Creazione latch(4)");
        latch = new CountDownLatch(4);
        this.refusedInvite = false;
    }
    public void addWaitingRival(String usernameRival)
    // This function add a new RivalWaiting only if it does not waiting yet
    {
        if (this.rivalsTeamWaiting.stream().filter(rivalWaiting -> rivalWaiting.equals(usernameRival)).toList().isEmpty())
        {
            this.rivalsTeamWaiting.add(usernameRival);
            this.latch.countDown();
            System.out.println("[R] CountDown: " + this.latch.getCount());
        }
        try { this.latch.await(); }
        catch (Exception e) { System.out.println("Errore eccezione await() [R] -> " + e.getMessage()); }

        //Modificato come addWaitingFriend per gestione navigazione tra pagine ed eventuali problemi di aggiornamento del counter
    }

    public void addWaitingFriend(String usernameFriend)
    // This function add a new FriendWaiting only if it does not waiting yet
    {
        if (this.inviterTeamWaiting.stream().filter(friendWaiting -> friendWaiting.equals(usernameFriend)).toList().isEmpty())
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

    public Boolean getRefusedInvite() { return this.refusedInvite; }

    public void setRefusedInvite(Boolean refusedInvite) { this.refusedInvite = refusedInvite; }
}
