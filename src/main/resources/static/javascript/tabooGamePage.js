const username = sessionStorage.getItem("userLog");
const IP_SERVER_ERLANG = "127.0.0.1";
let socket;

$(document).ready(function ()
{
    if (!checkLogin())
    {
        location.href = "../";
        return;
    }

    socket = new WebSocket("ws://" + IP_SERVER_ERLANG + "/erlServer");

    // INiziare a programmare il server erlang.


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
