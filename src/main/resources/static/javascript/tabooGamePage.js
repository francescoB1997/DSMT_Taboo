const username = sessionStorage.getItem("userLog");
const IP_SERVER_ERLANG = "127.0.0.1:8090";
let socket;

$(document).ready(function ()
{
    if (false && !checkLogin()) // Da togliere il false dalla condizione
    {
        location.href = "../";
        return;
    }

    socket = new WebSocket("ws://" + IP_SERVER_ERLANG + "/erlServer");
    socket.addEventListener("open", (event) => { sendInitMsg(); });
    socket.addEventListener("message", (event) => { msgOnSocketRecevedListener(event); });

    document.getElementById("btnSendMsg").onclick = function (e ) { onClickListenerBtnSendMsg(); }

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

function sendInitMsg()
{
    let loginMsg = {
        action : "login",
        username : username
    };
    socket.send(JSON.stringify(loginMsg));

    let myRole = sessionStorage.getItem("myRole");
    let matchJSON = sessionStorage.getItem("match");
    //alert("matchJSON = " + matchJSON );~
    let match = JSON.parse(matchJSON);

    let myTeam = extractMyTeam(match);
    if(myTeam != null) {
        let startMsg = {
            action: "start",
            friendList: myTeam,
            role: myRole
        };
        socket.send(JSON.stringify(startMsg));
    }
}

function msgOnSocketRecevedListener (event)
{
    alert("Ho ricevuto: " + event.data);
}

function extractMyTeam(match)
{
    if(match.inviterTeam[0] === username)
        return match.inviterTeam.filter( (friendUsername) => friendUsername !== username);

    if(match.rivalTeam[0] === username)
        return match.rivalTeam.filter( (friendUsername) => friendUsername !== username);

    return null;
}

function onClickListenerBtnSendMsg()
{
    let genericMsg = document.getElementById("txtboxGenericMsg").value;
    if(genericMsg === "")
        return;

    let actionGenericMsg =
        {
            action : "send_msg_to_friends",
            msg : genericMsg
        }
    socket.send(JSON.stringify(actionGenericMsg));
}