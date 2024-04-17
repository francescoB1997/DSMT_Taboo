const username = sessionStorage.getItem("userLog");
const IP_Server = "10.2.1.130:8084/DSMT_Taboo-0.0.1";

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }

    ajaxSendReplyInvitation();

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

function ajaxSendReplyInvitation()
{
    const inviteReplyJSON = sessionStorage.getItem("inviteReply");
    if(!inviteReplyJSON)
    {
        alert("No invite to wait");
        location.href = "../createTeamPage.html";
        return;
    }

    $.ajax({
        url: "http://" + IP_Server + "/replyInvite",
        type: "POST",
        data: inviteReplyJSON, // this inviteReply is alreay a JSON string
        contentType: 'application/json',
        success: function (serverResponse)
        {
            if(serverResponse.responseMessage != undefined) // If NOT_Undefined ==> All the users have accepted the invite
            {                                               // and the serverResponse cointaind all MATCH_INFO
                //alert("Tutti hanno accettato");
                sessionStorage.setItem("match", JSON.stringify(serverResponse.responseMessage));
                sessionStorage.removeItem("invite");
                location.href = "../tabooGamePage.html";
                // redirect alla pagina di gioco
            }
            else
            {
                alert("Qualcuno ha rifiutato");
                sessionStorage.removeItem("myRole");
                sessionStorage.removeItem("inviteReply");
                sessionStorage.removeItem("invite");
                location.href = "../startMatchPage.html";
            }
        },
        error: function ()
        {
            alert("Unauthorized Request!");
            location.href = "../";
        }
    });
    sessionStorage.removeItem("inviteReply");
}