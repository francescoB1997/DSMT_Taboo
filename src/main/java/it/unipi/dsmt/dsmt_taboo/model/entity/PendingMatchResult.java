package it.unipi.dsmt.dsmt_taboo.model.entity;

import java.util.concurrent.CountDownLatch;

public class PendingMatchResult
{
    private CountDownLatch latch;
    private Integer scoreInviterTeam;
    private Integer scoreRivalTeam;

    public PendingMatchResult()
    {
        this.latch = new CountDownLatch(2); // 2 perchè la useranno SOLO i 2 prompter( uno per ogni squadra)
        this.scoreInviterTeam = null;
        this.scoreRivalTeam = null;
    }

    public Integer getScoreInviterTeam() { return this.scoreInviterTeam; }

    public void setScoreInviterTeam(Integer scoreInviterTeam) // BLOCKING SETTER if you cannot bypass the Lock (bypassLock = false)
    {
        this.scoreInviterTeam = scoreInviterTeam;
        this.latch.countDown();
        try { this.latch.await(); }
        catch (Exception e) { System.out.println("Errore eccezione await() latch score [Inviter] -> " + e.getMessage()); }

    }

    public Integer getScoreRivalTeam() { return this.scoreRivalTeam; }

    public void setScoreRivalTeam(Integer scoreRivalTeam) // BLOCKING SETTER if you cannot bypass the Lock (bypassLock = false)
    {
        this.scoreRivalTeam = scoreRivalTeam;
        this.latch.countDown();
        try { this.latch.await(); }
        catch (Exception e) { System.out.println("Errore eccezione await() latch score [Rival] -> " + e.getMessage()); }
    }

    public Boolean canINonBlock()
    {
        return (this.latch.getCount() == 1);
    }
}
