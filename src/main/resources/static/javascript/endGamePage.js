const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "./";
        return;
    }
    ajaxGetMatchResult();
    document.getElementById("returnHomeBtn").onclick = function (e) { onClickListenerBtnReturnToHP(); };
});

function onClickListenerBtnReturnToHP()
{
    if(!checkLogin())
    {
        location.href = "./";
        return;
    }
    location.href = "./loggedPlayerPage.html"
}

function checkLogin()
{
    if(!username)
    {
        alert("You're not logged");
        return false;
    }
    return true;
}

function ajaxGetMatchResult()
// This function asks to the server the result of the just finished match (if the server yet know them!!!)
{
    const match = JSON.parse(sessionStorage.getItem("match"));
    const myTeam = sessionStorage.getItem("myTeam");

    if(myTeam == null || match == null)
        return;

    let matchResultRequest = { // We exploited the already ResultMatchDTO to ask the server the match result
        matchId : match.matchId,
        usernameRequester: username,
        scoreInviterTeam : null,
        scoreRivalTeam : null
    };

    $.ajax({
        url : "./getMatchResult",
        type : "POST",
        data : JSON.stringify(matchResultRequest),
        contentType: 'application/json',
        success: function (serverResponse)
        {
            const receivedMatchResult = serverResponse.responseMessage;
            if(myTeam === "inviterTeam")
            {
                if(receivedMatchResult.scoreInviterTeam > receivedMatchResult.scoreRivalTeam)
                    displayMatchResult(1, receivedMatchResult.scoreInviterTeam, receivedMatchResult.scoreRivalTeam);
                else if(receivedMatchResult.scoreInviterTeam < receivedMatchResult.scoreRivalTeam)
                    displayMatchResult(-1, receivedMatchResult.scoreInviterTeam, receivedMatchResult.scoreRivalTeam);
                else
                    displayMatchResult(0, receivedMatchResult.scoreInviterTeam, receivedMatchResult.scoreRivalTeam);
            }
            else if(myTeam === "rivalTeam")
            {
                if(receivedMatchResult.scoreRivalTeam > receivedMatchResult.scoreInviterTeam )
                    displayMatchResult(1, receivedMatchResult.scoreInviterTeam, receivedMatchResult.scoreRivalTeam);
                else if(receivedMatchResult.scoreRivalTeam < receivedMatchResult.scoreInviterTeam )
                    displayMatchResult(-1, receivedMatchResult.scoreInviterTeam, receivedMatchResult.scoreRivalTeam);
                else
                    displayMatchResult(0, receivedMatchResult.scoreInviterTeam, receivedMatchResult.scoreRivalTeam);
            }
            else
            {
                displayMatchResult(-2, null, null); // <-- Warning message
            }
        },
        error: function (xhr)
        {
            if(xhr.status === 400)
                displayMatchResult(-2, null, null); // <-- Warning message
            else
            {
                displayMatchResult(-3, null, null); // <-- Warning message
                location.href = "./";
            }
        }
    });
    sessionStorage.removeItem("matchResult");
}

function displayMatchResult(resultMatch, scoreInviterTeam, scoreRivalTeam)
{
    let divResult = document.getElementById("resultMatch");
    switch (resultMatch)
    {
        case 1:
            divResult.innerText = "You and your Team are\n\n *** WINNERS ***";
            break;
        case -1:
            divResult.innerText = "You and your Team  are\n\n *** LOSERS ***";
            break;
        case 0:
            divResult.innerText = "The Game Ended in a *** TIE ***";
            break;
        case -2:
            divResult.innerText = "Error: Service temporary unavailable";
            break;
        default:
            divResult.innerText = "Are you sure you've played a Game?";
            return;
    };
    divResult.innerText += "\n\n Match Result:\n Team RED " + scoreInviterTeam + " - " + scoreRivalTeam + " Team BLUE\n";
}