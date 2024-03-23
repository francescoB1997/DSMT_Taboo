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
    let match = JSON.parse(matchJSON);

    let friendList = extractMyTeam(myRole, match);
    if (friendList != null)
    {
        let startMsg = {
            action: "start",
            friendList: friendList,
            role: myRole
        };
        socket.send(JSON.stringify(startMsg));
    }
}

function msgOnSocketRecevedListener (event)
{
    //alert("Ho ricevuto: " + event.data);
    objectFromErlang = JSON.parse(event.data);
    switch(objectFromErlang.action)
    {
        case "msgFromFriend":
            alert("Message from friend: " + objectFromErlang.msg);
            startWait();
            break;
        case "tabooCard":
            alert("Ricevuta tabooCard: " + objectFromErlang.msg);
            startWait();
            break;
        default:
            break;
    }



}

function extractMyTeam(myRole, match)
{
    for(const user of match.inviterTeam )
    {
        if (user === username)
        {
            if (myRole === "Guesser")
            {
                let indexPrompter;
                for (indexPrompter = 0; indexPrompter < match.rolesInviterTeam.length; indexPrompter++)
                    if (match.rolesInviterTeam[indexPrompter] === "Prompter")
                        break;

                return [match.inviterTeam[indexPrompter]];
            }
            else
                return match.inviterTeam.filter((friendUsername) => friendUsername !== username);
        }
    }

    for(const user of match.rivalTeam )
    {
        if (user === username) {
            if (myRole === "Guesser") {
                let indexPrompter;
                for (indexPrompter = 0; indexPrompter < match.rolesRivalTeam.length; indexPrompter++)
                    if (match.rolesRivalTeam[indexPrompter] === "Prompter")
                        break;

                return [match.rivalTeam[indexPrompter]];
            } else
                return match.rivalTeam.filter((friendUsername) => friendUsername !== username);
        }
    }

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

function startWait()
{
    let waitMessage = {
        action : "wait"
    };
    if (socket.readyState === WebSocket.OPEN)
        socket.send(JSON.stringify(waitMessage));
    else
        console.error("Connessione WebSocket non aperta.");
}
