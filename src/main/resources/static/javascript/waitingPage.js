const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "./";
        return;
    }

    ajaxSendReplyInvitation();

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

function ajaxSendReplyInvitation()
{
    const inviteReplyJSON = sessionStorage.getItem("inviteReply");
    if(!inviteReplyJSON)
    {
        alert("No Invitation To Wait");
        location.href = "./createTeamPage.html";
        return;
    }

    $.ajax({
        url: "./replyInvite",
        type: "POST",
        data: inviteReplyJSON, // this inviteReply is alreay a JSON string
        contentType: 'application/json',
        success: function (serverResponse)
        {
            if(serverResponse.responseMessage != undefined) // If NOT_Undefined ==> All the users have accepted the invite
            {                                               // and the serverResponse contained all MATCH_INFO
                //All players have accepted the invitation
                sessionStorage.setItem("match", JSON.stringify(serverResponse.responseMessage));
                sessionStorage.removeItem("invite");
                location.href = "./tabooGamePage.html"; //redirect to the game page
            }
            else
            {
                alert("Someone Has Rejected The Invitation");
                sessionStorage.removeItem("myRole");
                sessionStorage.removeItem("inviteReply");
                sessionStorage.removeItem("invite");
                location.href = "./startMatchPage.html";
            }
        },
        error: function ()
        {
            alert("Unauthorized Request!");
            location.href = "./";
        }
    });
    sessionStorage.removeItem("inviteReply");
}