const username = sessionStorage.getItem("userLog");
const IP_Server = "10.2.1.130:8084/DSMT_Taboo-0.0.1";

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }
    ajaxGetMatchResult();
    document.getElementById("returnHomeBtn").onclick = function (e) { onClickListenerBtnReturnToHP(); };
});

function onClickListenerBtnReturnToHP()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }
    location.href = "../loggedPlayerPage.html"
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
{
    const match = JSON.parse(sessionStorage.getItem("match"));
    const myTeam = sessionStorage.getItem("myTeam");

    if(myTeam == null || match == null)
        return;

    let matchResultRequest = {
        matchId : match.matchId,
        usernameRequester: username,
        scoreInviterTeam : null,
        scoreRivalTeam : null
    };

    $.ajax({
        url : "http://" + IP_Server +  "/getMatchResult",
        type : "POST",
        data : JSON.stringify(matchResultRequest),
        contentType: 'application/json',
        success: function (serverResponse)
        {
            const receivedMatchResult = serverResponse.responseMessage;
            //alert("io sono di " + myTeam +  ": ScoreInv=" + receivedMatchResult.scoreInviterTeam + " | ScorRiv=" + receivedMatchResult.scoreRivalTeam);

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
                alert("strano valore myTeam: " + myTeam);
            }
        },
        error: function (xhr)
        {
            displayMatchResult(-2, null, null); // <-- Warning message
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
        default:
            divResult.innerText = "Are you sure you've played a Game?";
            return;
    };
    //Inviter 0 - 0 Rival
    divResult.innerText += "\n\n Match Result:\n Team RED " + scoreInviterTeam + " - " + scoreRivalTeam + " Team BLUE\n";
}