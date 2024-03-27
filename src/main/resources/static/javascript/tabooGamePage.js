const username = sessionStorage.getItem("userLog");
const IP_SERVER_ERLANG = "127.0.0.1:8090";
let socket;
let myRole;

$(document).ready(function ()
{
    if (false && !checkLogin()) // Da togliere il false dalla condizione
    {
        location.href = "../";
        return;
    }

    initAndConfigureSocket(undefined);

    document.getElementById("btnSendMsg").onclick = function (e ) { onClickListenerBtnSendMsg(); }
    document.getElementById("btnGuess").onclick = function (e ) { onClickListenerBtnGuess(); }

});

function initAndConfigureSocket(event)
{
    socket = new WebSocket("ws://" + IP_SERVER_ERLANG + "/erlServer");
    socket.addEventListener("open", (event) => { sendInitMsg(); });
    socket.addEventListener("message", (event) => { msgOnSocketRecevedListener(event); });

    socket.addEventListener("close", (event) => {
        console.log("socket chiuso, riapro");
        changeRoles();
        initAndConfigureSocket(event);
    });
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

function sendInitMsg()
{
    let loginMsg = {
        action : "login",
        username : username
    };
    socket.send(JSON.stringify(loginMsg));

    myRole = sessionStorage.getItem("myRole");
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
            break;
        case "tabooCard":
            alert("Ricevuta tabooCard: " + objectFromErlang.msg);
            break;
        case "attemptGuessWord":
            if(myRole==="Guesser")
                alert("Il Prompter mi ha detto: " + ((objectFromErlang.msg===true) ? "Indovinato" : "Sbagliato"));
            break;
        default:
            break;
    }
}

function extractMyTeam(myRole, match)
// This function returns the teamMembers of this user. In details filters-out this user from its own team, beacuse
// this information is used by ErlangServer to know the friend in team in order to send they one message of this user.
{
    for(const user of match.inviterTeam )
    {
        if (user === username)
        {
            /*if (myRole === "Guesser")
            {
                let indexPrompter;
                for (indexPrompter = 0; indexPrompter < match.rolesInviterTeam.length; indexPrompter++)
                    if (match.rolesInviterTeam[indexPrompter] === "Prompter")
                        break;

                return [match.inviterTeam[indexPrompter]];
            }
            else*/
                return match.inviterTeam.filter((friendUsername) => friendUsername !== username);
        }
    }

    for(const user of match.rivalTeam )
    {
        if (user === username) {
            /*
            if (myRole === "Guesser") {
                let indexPrompter;
                for (indexPrompter = 0; indexPrompter < match.rolesRivalTeam.length; indexPrompter++)
                    if (match.rolesRivalTeam[indexPrompter] === "Prompter")
                        break;

                return [match.rivalTeam[indexPrompter]];
            } else*/
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
        };
    socket.send(JSON.stringify(actionGenericMsg));
}

function onClickListenerBtnGuess()
{
    let attemptedWord = document.getElementById("txtboxGenericMsg").value;
    if(attemptedWord === "")
        return;

    if(attemptedWord.includes(" "))
    {
        document.getElementById("txtboxGenericMsg").value = "";
        alert("WARNING\nYou can only send one word");
        return;
    }

    let actionGuess =
        {
            action : "attemptGuessWord",
            word : attemptedWord
        };
    socket.send(JSON.stringify(actionGuess));
}

function findMyPrompter()
{
    //let myRole = sessionStorage.getItem("myRole");
    let matchJSON = sessionStorage.getItem("match");
    let match = JSON.parse(matchJSON);
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
        }
    }

    for(const user of match.rivalTeam ) {
        if (user === username)
        {
            if (myRole === "Guesser") {
                let indexPrompter;
                for (indexPrompter = 0; indexPrompter < match.rolesRivalTeam.length; indexPrompter++)
                    if (match.rolesRivalTeam[indexPrompter] === "Prompter")
                        break;

                return [match.rivalTeam[indexPrompter]];
            }
        }
    }
}

function changeRoles()
{
    sessionStorage.removeItem("myRole");
    let matchJSON = sessionStorage.getItem("match");
    let match = JSON.parse(matchJSON);
    let myTeam = sessionStorage.getItem("myTeam");

    if(myTeam === "inviterTeam"){

        const posPrompter = match.rolesInviterTeam.findIndex(role => role === 'Prompter');

        if (posPrompter !== -1)
        {
            const newPosPrompter = (posPrompter + 1) % match.rolesInviterTeam.length;
            match.rolesInviterTeam[posPrompter] = 'Guesser';
            match.rolesInviterTeam[newPosPrompter] = 'Prompter';
        }
        const myPosInTeam = match.inviterTeam.findIndex(name => name === username);
        myRole =  match.rolesInviterTeam[myPosInTeam];

        sessionStorage.setItem("myRole", myRole);
    }
    else {

        const posPrompter = match.rivalTeam.findIndex(role => role === 'Prompter');

        if (posPrompter !== -1)
        {
            const newPosPrompter = (posPrompter + 1) % match.rivalTeam.length;
            match.rivalTeam[posPrompter] = 'Guesser';
            match.rivalTeam[newPosPrompter] = 'Prompter';
        }
        const myPosInTeam = match.rivalTeam.findIndex(name => name === username);
        myRole =  match.rivalTeam[myPosInTeam];

        sessionStorage.setItem("myRole", myRole);
    }
}