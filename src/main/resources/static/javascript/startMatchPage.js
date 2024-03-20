const username = sessionStorage.getItem("userLog");
$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }

    document.getElementById("btnCreateTeam").onclick = function (e) { onClickListenerBtnCreateTeam(); };
    document.getElementById("btnCheckInvite").onclick = function (e) { onClickListenerBtnCheckInvite(); };
});

function checkLogin()
{
    if(!username)
    {
        alert("You're not logged");
        return false;
    }
    return true;
}

function onClickListenerBtnCreateTeam()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }

    ajaxCheckInvite(true);
}

function onClickListenerBtnCheckInvite()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }
    ajaxCheckInvite();
}

function ajaxCheckInvite(isCreatYourTeamBtn)
{
    $.ajax({
        url: "http://localhost:8080/checkInvite",
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
                location.href = "../createTeamPage.html";
                return;
            } else if (invite === undefined)
            {
                alert("Nessun invito");
                sessionStorage.removeItem("invite");
                sessionStorage.removeItem("myRole");
                return;
            }

            if(invite.rivals[0] === username)   // Check if this user is the first Rival, that has the power to Create its (Rival)Team
            {
                let msgForCreateTeamBtn = (isCreatYourTeamBtn === true) ? "Notification: Before Create Your team must know that " : "";
                sessionStorage.setItem("myRole", "rival1"); // Role 'rival1' will create its team
                let msgToShow = msgForCreateTeamBtn + "You've been invited from [" + invite.userInviter + "] as RIVAL.\nAccept to create your Rival Team";
                const inviteResponse = window.confirm(msgToShow);
                if (inviteResponse)
                {
                    sessionStorage.setItem("invite", JSON.stringify(invite));
                    location.href = "../createRivalTeamPage.html";
                }
                else
                {
                    storeInvitation(false, invite.gameId, false);
                    sessionStorage.removeItem("invite");
                    sessionStorage.removeItem("myRole");
                }
                return;
            }

            for (let i = 1; i < invite.rivals.length; i++) // this loop MUST start at 1. Check if the user has been invited in RivalTeam
            {
                if (invite.rivals[i] === username)
                {
                    let msgForCreateTeamBtn = (isCreatYourTeamBtn === true) ? "Notification: Before Create Your team must know that " : "";
                    sessionStorage.setItem("myRole", "rival2");
                    let msgToShow = msgForCreateTeamBtn + "You've been invited from [" + invite.rivals[0] + "] as RIVAL of [" + invite.userInviter + "]\n" +
                        "Do you accept the invite?";
                    const inviteResponse = confirm(msgToShow);
                    if(inviteResponse)
                        sessionStorage.setItem("invite", JSON.stringify(invite));
                    else {
                        sessionStorage.removeItem("invite");
                        sessionStorage.removeItem("myRole");
                    }
                    storeInvitation(inviteResponse, invite.gameId, false);
                    return; //Here there is the return because has no sense to continue with other foreach loop
                }
            }

            for (let i = 0; i < invite.yourTeam.length; i++)
            //for(let inTeamFriend of invite.yourTeam)    // check if this user has been invited in FriendTeam
            {
                if(invite.yourTeam[i] === username)
                {
                    let msgForCreateTeamBtn = (isCreatYourTeamBtn === true) ? "Notification: Before Create Your team must know that " : "";
                    sessionStorage.setItem("myRole", invite.roles[i]);
                    let msgToShow = msgForCreateTeamBtn + "You've been invited from [" + invite.userInviter + "] as FRIEND.\nDo you accept the invite?";
                    const inviteResponse = confirm(msgToShow);
                    if(inviteResponse)
                        sessionStorage.setItem("invite", JSON.stringify(invite));
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
            location.href = "../";
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
    location.href = "../waitingPage.html";
}