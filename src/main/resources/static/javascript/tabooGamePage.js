const username = sessionStorage.getItem("userLog");
const IP_SERVER_ERLANG = "127.0.0.1:8090";
let myRole = sessionStorage.getItem("myRole");
let timerInterval;
var seconds = 20;
var stopCondition = 0;
let socket;

$(document).ready(function ()
{
    if (false && !checkLogin()) // Da togliere il false dalla condizione
    {
        location.href = "../";
        return;
    }

    initAndConfigureSocket(undefined);

    document.getElementById("btnSendMsg").onclick = function (e ) { onClickListenerBtnSendMsg(); }
    document.getElementById("restart-button").onclick = function (e ) { restartGame(); }
    document.getElementById("btnTabooWord").onclick = function (e ) { }

    document.getElementById("btnGuess").onclick = function (e) { onClickListenerBtnGuess(); }
    document.getElementById("pass-button").onclick = function (e) { onClickListenerBtnPass(); }

    changeVisibilityBtn("btnGuess",myRole === 'Guesser');
    changeVisibilityBtn("pass-button",myRole === 'Prompter');

});

function initAndConfigureSocket(event)
{
    socket = new WebSocket("ws://" + IP_SERVER_ERLANG + "/erlServer");
    socket.addEventListener("open", (event) => { sendInitMsg();});
    socket.addEventListener("message", (event) => { msgOnSocketRecevedListener(event);});

    socket.addEventListener("close", (event) => {
        console.log("socket chiuso, riapro");
        initAndConfigureSocket(event);
    });
}

function restartGame(){
    changeRoles();
    socket.close();
}

function timerHandler()
{
    seconds -= 1;
    document.getElementById("timer").textContent = seconds;
    if(seconds <= 0) {
        clearInterval(timerInterval);
        seconds = 20;
        restartGame();
    }
}

function changeVisibilityBtn (buttonId, visible)
{
    let button = document.getElementById(buttonId);
    if(visible)
    {
        button.classList.replace("disabled", "enabled");
        button.disable = false;
        if(buttonId === "btnGuess")
            button.onclick = function (e) { onClickListenerBtnGuess(); }
        else
            button.onclick = function (e) { onClickListenerBtnPass(); }
    }
    else
    {
        button.onclick = null;
        button.classList.replace("enabled", "disabled");
        button.disable = true;
    }
}

function onClickListenerBtnPass()
{
    alert("Comportamento cambio card con nuova parola da far indovinare");
    return;
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

    let friendList = extractMyTeam(match);
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
            //alert("Ricevuta tabooCard: " + objectFromErlang.msg);
            clearInterval(timerInterval);
            timerInterval = setInterval(timerHandler, 1000);
            break;
        case "timerGuesser":
            clearInterval(timerInterval);
            timerInterval = setInterval(timerHandler, 1000);
            break;
        case "attemptGuessWord":
            if(myRole==="Guesser")
                alert("Il Prompter mi ha detto: " + ((objectFromErlang.msg===true) ? "Indovinato" : "Sbagliato"));
            break;
        default:
            break;
    }
}

function extractMyTeam(match)
// This function returns the teamMembers of this user. In details filters-out this user from its own team, beacuse
// this information is used by ErlangServer to know the friend in team in order to send they one message of this user.
{
    for(const user of match.inviterTeam )
    {
        if (user === username)
        {
            sessionStorage.setItem("myTeam", "inviterTeam");
            return match.inviterTeam.filter((friendUsername) => friendUsername !== username);
        }
    }

    for(const user of match.rivalTeam )
    {
        if (user === username)
        {
            sessionStorage.setItem("myTeam", "rivalTeam");
            return match.rivalTeam.filter((friendUsername) => friendUsername !== username);
        }
    }
    return null;
}

function onClickListenerBtnSendMsg()
{
    let genericMsg = document.getElementById("txtboxGenericMsg").value;
    if(genericMsg === ""){
        alert("WARNING\nThe text-box is empty");
        return;
    }

    let actionGenericMsg =
        {
            action : "send_msg_to_friends",
            msg : genericMsg
        };
    socket.send(JSON.stringify(actionGenericMsg));
    document.getElementById("txtboxGenericMsg").value = "";
}

function onClickListenerBtnGuess()
{
    let attemptedWord = document.getElementById("txtboxGenericMsg").value;
    if(attemptedWord === ""){
        alert("WARNING\nThe text-box is empty");
        return;
    }

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
    document.getElementById("txtboxGenericMsg").value = ""
}

function changeRoles()
{
    sessionStorage.removeItem("myRole");
    let matchJSON = sessionStorage.getItem("match");
    let match = JSON.parse(matchJSON);
    let myTeam = sessionStorage.getItem("myTeam");

    if(myTeam === "inviterTeam")
    {
        const posPrompter = match.rolesInviterTeam.findIndex(role => role === 'Prompter');

        if (posPrompter !== -1)
        {
            const newPosPrompter = (posPrompter + 1) % match.rolesInviterTeam.length;
            match.rolesInviterTeam[posPrompter] = 'Guesser';
            match.rolesInviterTeam[newPosPrompter] = 'Prompter';
        }
        const myPosInTeam = match.inviterTeam.findIndex(name => name === username);
        myRole = match.rolesInviterTeam[myPosInTeam];
    }
    else {
        const posPrompter = match.rolesRivalTeam.findIndex(role => role === 'Prompter');

        if (posPrompter !== -1)
        {
            const newPosPrompter = (posPrompter + 1) % match.rolesRivalTeam.length;
            match.rolesRivalTeam[posPrompter] = 'Guesser';
            match.rolesRivalTeam[newPosPrompter] = 'Prompter';
        }
        const myPosInTeam = match.rivalTeam.findIndex(name => name === username);
        myRole =  match.rolesRivalTeam[myPosInTeam];
    }

    sessionStorage.setItem("myRole", myRole);
    sessionStorage.setItem("match", JSON.stringify(match));

    changeVisibilityBtn("btnGuess",myRole === 'Guesser');
    changeVisibilityBtn("pass-button",myRole === 'Prompter');

    stopCondition++;
    if (stopCondition === match.rolesInviterTeam.length)
    {
        //Stop The Game and insert match into MySQL DB
        if(myRole === "Prompter" && myTeam === "inviterTeam")
            addNewMatch();
        location.href = "../endGamePage.html";
    }
}

function addNewMatch()
{
    let matchJSON = sessionStorage.getItem("match");
    let match = JSON.parse(matchJSON);

    $.ajax({
        url : "http://localhost:8080/addNewMatch",
        data : JSON.stringify(match),
        type : "POST",
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let addMatchOperation = serverResponse.responseMessage;
            switch (addMatchOperation)
            {
                case 0:
                    console.log("The match has been successfully added into DB.");
                    break;
                case 1:
                    console.log("We're Sorry, an Error occurred during adding operation." +
                        " The match has not been successfully added into DB");
                    break;
                default:
                    //alert("Default: " + responseMessage);
                    break;
            }
        },
        error: function (xhr)
        {
            let responseMessage = xhr.responseText;
            alert("Error: " + responseMessage);
        }
    });
}