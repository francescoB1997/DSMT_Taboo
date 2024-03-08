const username = sessionStorage.getItem("userLog");
$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }

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

function onClickListenerBtnCheckInvite()
{
    $.ajax({
        url: "http://localhost:8080/checkInvite",
        type: "POST",
        data: username,
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let invite = serverResponse.responseMessage;
            if(invite === undefined) {
                alert("Nessun invito");
                sessionStorage.setItem("rivalTeam", null);
                return;
            }

            if(invite.rivals[0] === username)   // Check if this user is the first Rival, that has the power to Create its (Rival)Team
            {
                const inviteResponse = confirm("You've been invited from " + invite.userInviter + "as Rival. Accept to create your Rival Team");
                if (inviteResponse) {
                    sessionStorage.setItem("invite", JSON.stringify(invite));
                    location.href = "../createRivalTeamPage.html";
                } else {
                    sessionStorage.setItem("invite", null);
                    ajaxSendRefusedInvitation();
                }
            }

            for(let i = 1; i < invite.rivals.length; i++) // this for MUST start at 1. Check if the user has been invited in RivalTeam
            {
                if (invite.rivals[i] === username)
                {
                    alert("Sei nella squadra dei rivali");
                    return; //Here there is the return because has no sense to continue with other foreach
                }
            }

            for(let inTeamFriend of invite.yourTeam)    // check if this user has been invited in FriendTeam
            {
                if(inTeamFriend === username)
                {
                    alert("io sono in squadra con " + invite.userInviter);
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

function getRandomInt(min , max)
{
    let randomInt = 0;
    while(randomInt === 0) {
        min = Math.ceil(min);
        max = Math.floor(max);
        randomInt = Math.floor(Math.random() * (max - min + 1)) + min;
    }
    return randomInt;
}

function ajaxSendRefusedInvitation()
{
    $.ajax({
        url: "http://localhost:8080/refuseInvite",
        type: "POST",
        data: username,
        contentType: 'application/json',
        success: function (serverResponse)
        {
            alert("Invito rifiutato ACK");
        },
        error: function ()
        {
            alert("Unauthorized Request!");
            location.href = "../";
        }
    });
}