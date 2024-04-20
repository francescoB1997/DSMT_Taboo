const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "./";
        return;
    }

    document.getElementById("btnCreateTeam").onclick = function (e) { onClickListenerBtnCreateTeam(); };
    document.getElementById("btnCheckInvite").onclick = function (e) { onClickListenerBtnCheckInvite(); };
});

function checkLogin()
{
    if(!username)
    {
        alert("You're Not Logged");
        return false;
    }
    return true;
}

function onClickListenerBtnCreateTeam()
{
    if(!checkLogin())
    {
        location.href = "./";
        return;
    }

    ajaxCheckInvite(true);
}

function onClickListenerBtnCheckInvite()
{
    if(!checkLogin())
    {
        location.href = "./";
        return;
    }
    ajaxCheckInvite();
}

function ajaxCheckInvite(isCreatYourTeamBtn)
{
    $.ajax({
        url: "./checkInvite",
        type: "POST",
        data: username,
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let invite = serverResponse.responseMessage;
            if( (invite === undefined) && (isCreatYourTeamBtn === true))
            {
                sessionStorage.removeItem("invite");
                sessionStorage.removeItem("myRole");
                location.href = "./createTeamPage.html";
                return;
            } else if (invite === undefined)
            {
                alert("No Invitation");
                sessionStorage.removeItem("invite");
                sessionStorage.removeItem("myRole");
                return;
            }

            if(invite.rivals[0] === username) // Check if this user is the first Rival, that has the power to Create its (Rival)Team
            {
                let msgForCreateTeamBtn = (isCreatYourTeamBtn === true) ? "Notification: Before Create Your Team Must Know That " : "";
                let msgToShow = msgForCreateTeamBtn + "You've Been Invited From [" + invite.userInviter + "] as RIVAL.\n\nAccept to create your Rival Team";
                const inviteResponse = window.confirm(msgToShow);
                if (inviteResponse)
                {
                    sessionStorage.setItem("invite", JSON.stringify(invite));
                    location.href = "./createRivalTeamPage.html";
                }
                else
                {
                    storeInvitation(false, invite.gameId, false);
                    sessionStorage.removeItem("invite");
                    sessionStorage.removeItem("myRole");
                }
                return;
            }

            for (let i = 1; i < invite.rivals.length; i++) // starts form 1 because 0 is the inviter. Check if the user has been invited in RivalTeam
            {
                if (invite.rivals[i] === username)
                {
                    let msgForCreateTeamBtn = (isCreatYourTeamBtn === true) ? "Notification: Before Create Your Team Must Know That " : "";
                    let msgToShow = msgForCreateTeamBtn + "You've Been Invited From [" + invite.rivals[0] + "]" +
                                                        "as " + invite.rivalsRoles[i] + " RIVAL of [" + invite.userInviter + "]\n" +
                                                        "\nDo You Accept The Invitation?";
                    const inviteResponse = confirm(msgToShow);
                    if(inviteResponse) {
                        sessionStorage.setItem("invite", JSON.stringify(invite));
                        sessionStorage.setItem("myRole", invite.rivalsRoles[i]);
                    }
                    else {
                        sessionStorage.removeItem("invite");
                        sessionStorage.removeItem("myRole");
                    }
                    storeInvitation(inviteResponse, invite.gameId, false);
                    return; //to avoid to continue with other foreach loop
                }
            }

            for (let i = 0; i < invite.yourTeam.length; i++) // check if this user has been invited in FriendTeam
            {
                if(invite.yourTeam[i] === username)
                {
                    let msgForCreateTeamBtn = (isCreatYourTeamBtn === true) ? "Notification: Before Create Your Team Must Know That " : "";
                    let msgToShow = msgForCreateTeamBtn + "You've Been Invited From [" + invite.userInviter + "] " +
                                                          "as " + invite.roles[i] + " FRIEND." +
                                                          "\nDo You Accept The Invitation?";
                    const inviteResponse = confirm(msgToShow);
                    if(inviteResponse)
                    {
                        sessionStorage.setItem("invite", JSON.stringify(invite));
                        sessionStorage.setItem("myRole", invite.roles[i]);
                    }
                    else {
                        sessionStorage.removeItem("invite");
                        sessionStorage.removeItem("myRole");
                    }
                    storeInvitation(inviteResponse, invite.gameId, true);
                    break;
                }
            }
        },
        error: function ()
        {
            alert("Unauthorized Request!");
            location.href = "./";
        }
    });
}

function storeInvitation(accepted, inviteId, invitedAsFriend)
{
    let inviteReply =
        {
            senderUsername: username,
            gameId: inviteId,
            inviteState: accepted,
            invitedAsFriend: invitedAsFriend
        };
    sessionStorage.setItem("inviteReply", JSON.stringify(inviteReply));
    location.href = "./waitingPage.html";
}