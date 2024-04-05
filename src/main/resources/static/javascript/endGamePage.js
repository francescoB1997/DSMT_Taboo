const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }
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