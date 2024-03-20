const username = sessionStorage.getItem("userLog");
const IP_SERVER_ERLANG = "127.0.0.1:8090";
let socket;

$(document).ready(function ()
{
    if (false && !checkLogin())
    {
        location.href = "../";
        return;
    }

    socket = new WebSocket("ws://" + IP_SERVER_ERLANG + "/erlServer");

    socket.addEventListener("open", (event) => { sendInitMsg(); });

    socket.addEventListener("message", (event) => { msgOnSocketRecevedListener(event); });

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
    //alert("matchJSON = " + matchJSON );
    let match = JSON.parse(matchJSON);

    // username = fra2
    // inviterTeam -> [fra] [fra2]          rivalTeam -> [gsf] [cia]

    let myTeam = extractMyTeam(match);
    let temp_team = ["fra2", "gsf", "cia"];
    if(myTeam != null) {
        let startMsg = {
            action: "start",
            //friend1: String(myTeam[1]),
            friend1: temp_team,
            //friend2 : "ciao",
            role: myRole
        };
        socket.send(JSON.stringify(startMsg));
        //socket.send(JSON.stringify(loginMsg));
    }

    if(username === "fra")
    {
        let msgProva = {
            action : "send_msg_to_friends",
            msg : "amico mi ricevi?"
        }
        socket.send(JSON.stringify(msgProva));
    }
}

function msgOnSocketRecevedListener (event)
{
    alert("Ho ricevuto: " + event.data);
}

function extractMyTeam(match)
{
    if(match.inviterTeam[0] === username)
        return match.inviterTeam;

    if(match.rivalTeam[0] === username)
        return match.rivalTeam;

    return null;
}