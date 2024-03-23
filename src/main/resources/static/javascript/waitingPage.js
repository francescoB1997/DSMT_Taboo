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
        alert("No invite to wait");
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
            if(serverResponse.responseMessage != undefined) // If NOT undefined ==> All the users have accepted the invite
            {
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