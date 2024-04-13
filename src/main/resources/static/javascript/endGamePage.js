const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }
    ajaxGetMatchResult();
    displayMatchResult();
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




    sessionStorage.removeItem("matchResult");
}

function displayMatchResult(resultMatch)
{
    //const resultMatch = parseInt(sessionStorage.getItem("matchResult"));

    let divResult = document.getElementById("resultMatch");
    switch (resultMatch)
    {
        case 1:
            divResult.innerText = "You and Your team are the *** WINNERS *** of the Game";
            break;
        case -1:
            divResult.innerText = "You and Your team are the *** LOSERS *** of the Game";
            break;
        case 0:
            divResult.innerText = "The Game Ended in a *** TIE ***";
            break;
        default:
            divResult.innerText = "Are you sure you've played a game?";
            break;
    };
}