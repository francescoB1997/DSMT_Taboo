package it.unipi.dsmt.dsmt_taboo.model.entity;

import java.util.ArrayList;

public class TeamCreationWaiting {

    int numPrompters = 0;
    int numGuessers = 0;
    int numControllers = 0;
    ArrayList<String> usernamePrompters;
    ArrayList<String> usernameGuesser;
    ArrayList<String> usernameControllers;

    boolean invitationDeclined = false;

    public TeamCreationWaiting(int numPrompters, int numGuessers, int numControllers,
                               ArrayList<String> usernamePrompters,
                               ArrayList<String> usernameGuesser,
                               ArrayList<String> usernameControllers)
    {
        this.numPrompters = numPrompters;
        this.numGuessers = numGuessers;
        this.numControllers = numControllers;
        this.usernamePrompters = usernamePrompters;
        this.usernameGuesser = usernameGuesser;
        this.usernameControllers = usernameControllers;
    }

    public TeamCreationWaiting(int numPrompters, int numGuessers, int numControllers)
    {
        this.numPrompters = numPrompters;
        this.numGuessers = numGuessers;
        this.numControllers = numControllers;
        this.usernamePrompters = new ArrayList<>();
        this.usernameGuesser = new ArrayList<>();
        this.usernameControllers = new ArrayList<>();
        this.invitationDeclined = false;
    }

    public int getNumPrompters() {
        return numPrompters;
    }

    public void setNumPrompters(int numPrompters) {
        this.numPrompters = numPrompters;
    }

    public int getNumGuessers() {
        return numGuessers;
    }

    public void setNumGuessers(int numGuessers) {
        this.numGuessers = numGuessers;
    }

    public int getNumControllers() {
        return numControllers;
    }

    public void setNumControllers(int numControllers) {
        this.numControllers = numControllers;
    }

    public ArrayList<String> getUsernamePrompters() {
        return usernamePrompters;
    }

    public void setUsernamePrompters(ArrayList<String> usernamePrompters) {
        this.usernamePrompters = usernamePrompters;
    }

    public ArrayList<String> getUsernameGuesser() {
        return usernameGuesser;
    }

    public void setUsernameGuesser(ArrayList<String> usernameGuesser) {
        this.usernameGuesser = usernameGuesser;
    }

    public ArrayList<String> getUsernameControllers() {
        return usernameControllers;
    }

    public void setUsernameControllers(ArrayList<String> usernameControllers) {
        this.usernameControllers = usernameControllers;
    }

    public boolean isInvitationDeclined() {
        return invitationDeclined;
    }

    public void setInvitationDeclined(boolean invitationDeclined) {
        this.invitationDeclined = invitationDeclined;
    }
}
