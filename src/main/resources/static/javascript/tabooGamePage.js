const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    if (!checkLogin()) {
        location.href = "../";
        return;
    }





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
