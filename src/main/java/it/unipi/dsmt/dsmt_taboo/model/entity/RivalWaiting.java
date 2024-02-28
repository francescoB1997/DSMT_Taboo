package it.unipi.dsmt.dsmt_taboo.model.entity;

import java.util.ArrayList;

public class RivalWaiting {

    int numUsersInviters = 0;
    ArrayList<String> usernameUsersInviters;

    boolean invitationDeclined = false;

    public RivalWaiting(int numUsersInviters, ArrayList<String> usernameUsersInviters)
    {
        this.numUsersInviters = numUsersInviters;
        this.usernameUsersInviters = usernameUsersInviters;
    }

    public RivalWaiting(int numUsersInviters)
    {
        this.numUsersInviters = numUsersInviters;
        this.usernameUsersInviters = new ArrayList<>();
        this.invitationDeclined = false;
    }
}
