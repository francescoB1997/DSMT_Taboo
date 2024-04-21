package it.unipi.dsmt.dsmt_taboo.model.entity;

import java.util.concurrent.CountDownLatch;

public class PendingMatchResult
{
    /**
     * This class represents a pending match result, which is used to synchronize the reporting of match scores
     * from both the inviting team and the rival team. It includes a CountDownLatch object initialized with a
     * count of 2, indicating that it will be used by two prompters, one for each team. The class provides methods
     * to set and retrieve the scores for both teams, with the setters being blocking methods that decrement the
     * latch count and await completion of the score reporting process. The class facilitates the coordination
     * of match score reporting within the application.
     */

    private CountDownLatch latch;
    private Integer scoreInviterTeam;
    private Integer scoreRivalTeam;

    /**
     * Constructs a PendingMatchResult object with an initialized CountDownLatch with a count of 2,
     * representing the two prompters (one for each team).
     */
    public PendingMatchResult()
    {
        this.latch = new CountDownLatch(2);
        this.scoreInviterTeam = null;
        this.scoreRivalTeam = null;
    }

    /**
     * Constructs a PendingMatchResult object with specified scores for both teams.
     * @param scoreInviterTeam The score of the inviting team.
     * @param scoreRivalTeam The score of the rival team.
     */
    public PendingMatchResult(Integer scoreInviterTeam, Integer scoreRivalTeam)
    {
        if(scoreInviterTeam != null && scoreRivalTeam != null)
        {
            this.scoreInviterTeam = scoreInviterTeam;
            this.scoreRivalTeam = scoreRivalTeam;
            this.latch = null;
        }
    }

    /**
     * Retrieves the score of the inviting team.
     * @return The score of the inviting team.
     */
    public Integer getScoreInviterTeam() { return this.scoreInviterTeam; }

    /**
     * Sets the score of the inviting team and decrements the latch count.
     * This method is blocking and waits until the latch count reaches zero.
     * @param scoreInviterTeam The score of the inviting team.
     */
    public void setScoreInviterTeam(Integer scoreInviterTeam)
    {
        this.scoreInviterTeam = scoreInviterTeam;
        this.latch.countDown();
        try { this.latch.await(); } catch (Exception e) {
            System.out.println("Errore eccezione await() latch score [Inviter] -> " + e.getMessage());
        }
    }

    /**
     * Retrieves the score of the rival team.
     * @return The score of the rival team.
     */
    public Integer getScoreRivalTeam() { return this.scoreRivalTeam; }

    /**
     * Sets the score of the rival team and decrements the latch count.
     * This method is blocking and waits until the latch count reaches zero.
     * @param scoreRivalTeam The score of the rival team.
     */
    public void setScoreRivalTeam(Integer scoreRivalTeam)
    {
        this.scoreRivalTeam = scoreRivalTeam;
        this.latch.countDown();
        try { this.latch.await(); } catch (Exception e) {
            System.out.println("Error: await() exception. Latch score [Rival] -> " + e.getMessage());
        }
    }
}
