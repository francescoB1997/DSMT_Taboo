const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }
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

function displayMatchResult()
{
    const resultMatch = parseInt(sessionStorage.getItem("matchResult"));
    sessionStorage.removeItem("matchResult");

    if(resultMatch === 1)
    {
        alert("You and Your team are the *** WINNERS *** of the Game");
    } else if (resultMatch === -1 )
    {
        alert("You and Your team are the *** LOSERS *** of the Game");
    } else {
        alert("\nThe Game Ended in a *** TIE ***." + "\n\n- Play Again and Try to Do Better! -");
    }
}