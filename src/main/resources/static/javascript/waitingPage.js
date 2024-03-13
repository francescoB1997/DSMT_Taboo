const username = sessionStorage.getItem("userLog");
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
        alert("No inviteReply object in SessionStorgare!");
        location.href = "../createTeamPage.html";
        return;
    }

    $.ajax({
        url: "http://localhost:8080/replyInvite",
        type: "POST",
        data: inviteReplyJSON, // this inviteReply is alreay a JSON string
        contentType: 'application/json',
        success: function (serverResponse)
        {
            alert("Risposta: " + serverResponse.responseMessage);
            // se rifiutato => href a startAMatch
            //alert("[DBG] Tutti hanno accettato.\nIl gioco può iniziare");

        },
        error: function ()
        {
            alert("Unauthorized Request!");
            location.href = "../";
        }
    });
    sessionStorage.removeItem("inviteReply");
}