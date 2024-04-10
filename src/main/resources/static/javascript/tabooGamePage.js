const IP_SERVER_ERLANG = "127.0.0.1:8090";
const GAME_DURATION = 60;

const username = sessionStorage.getItem("userLog");
let myRole = sessionStorage.getItem("myRole");
let timerInterval;
let seconds= GAME_DURATION;
let stopCondition = 0;
let score = 0;

let prompterData = {
    tabooCard : null,
    passCounter : 0
};
let socket;

$(document).ready(function ()
{

    if (false && !checkLogin()) // Da togliere il false dalla condizione
    {
        location.href = "../";
        return;
    }

    setWelcomeText();
    initAndConfigureSocket(undefined);


    document.getElementById("btnSendMsg").onclick = function (e ) { onClickListenerBtnSendMsg(); }
    document.getElementById("btnTabooWord").onclick = function (e ) { }

    document.getElementById("btnGuess").onclick = function (e) { onClickListenerBtnGuess(); }
    document.getElementById("pass-button").onclick = function (e) { onClickListenerBtnPass(); }

    document.getElementById("txtboxGenericMsg").addEventListener("keypress", handlerEnterKeyPress);

    changeVisibilityBtn("btnGuess",myRole === 'Guesser');
    changeVisibilityBtn("pass-button",myRole === 'Prompter');

    setInterval(keepAlive, 20000);

    if(myRole === "Guesser")
        updateViewTabooCard();
    updateViewRole();
    document.getElementById("timer" ).innerText = GAME_DURATION;
});

function setWelcomeText()
{
    let divWelcome = document.getElementById("usernameField");
    divWelcome.innerHTML = username;
}

function keepAlive()
{
    let keepAliveMsg = {action: "keepAlive"};
    socket.send(JSON.stringify(keepAliveMsg));
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

function handlerEnterKeyPress(event)
{
    if (event.key === "Enter") {
        event.preventDefault(); // Per evitare l'invio del modulo (se presente)
        onClickListenerBtnSendMsg();
    }
}

function initAndConfigureSocket(event)
{
    socket = new WebSocket("ws://" + IP_SERVER_ERLANG + "/erlServer");
    socket.addEventListener("open", (event) => { sendInitMsg();});
    socket.addEventListener("message", (event) => { msgOnSocketRecevedListener(event);});

    socket.addEventListener("close", (event) => {
        console.log("socket chiuso, riapro");
        clearInterval(timerInterval);
        initAndConfigureSocket(event);
    });
}

function restartGame()
{
    changeRoles();
    socket.close();
}

function timerHandler()
{
    seconds -= 1;
    document.getElementById("timer").textContent = seconds;
    if(seconds <= 0)
    {
        clearInterval(timerInterval);
        seconds = GAME_DURATION;
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
            button.onclick = function (e) { onClickListenerBtnGuess(); };
        else
            button.onclick = function (e) { onClickListenerBtnPass(); };
    }
    else
    {
        button.onclick = null;
        button.classList.replace("enabled", "disabled");
        button.disable = true;
    }
}

function sendInitMsg()
{
    myRole = sessionStorage.getItem("myRole");
    let matchJSON = sessionStorage.getItem("match");
    let match = JSON.parse(matchJSON);

    loadTablesTeams();

    let friendList = extractMyTeam(match);
    if (friendList != null)
    {
        let loginMsg = {
            action: "login",
            username: username,
            friendList: friendList,
            role: myRole
        };
        socket.send(JSON.stringify(loginMsg));
    }
}

function msgOnSocketRecevedListener (event)
{
    //alert("Ho ricevuto: " + event.data);
    objectFromErlang = JSON.parse(event.data);
    switch(objectFromErlang.action)
    {
        case "loginOk":
            if(myRole === "Prompter")
            {
                let assignTabooCardMsg = { action : "assignTabooCard" };
                console.log("Inviato taboocard, attendo 2 secondi e invio start...");
                socket.send(JSON.stringify(assignTabooCardMsg));

                setTimeout(function() {
                    console.log("start inviato");
                    let startGameMsg = { action : "startGame"};
                    socket.send(JSON.stringify(startGameMsg));
                    clearInterval(timerInterval);
                    timerInterval = setInterval(timerHandler, 1000);
                },1000);
            }
            else
            {
                alert("Non ha senso che tu sia qui");
            }
            break;
        case "wakeUpGuesser":
            if(myRole === "Guesser")
            {
                console.log("Sono un guesser, ed il gioco può partire");
                clearInterval(timerInterval);
                timerInterval = setInterval(timerHandler, 1000);
            }
            break;
        case "tabooCard":
            prompterData.tabooCard = objectFromErlang.msg;
            updateViewTabooCard();
            if(myRole === "Prompter")
                console.log("Ricevuta TabooCard: " + prompterData.tabooCard);
            break;
        case "checkWordResult":
            if (objectFromErlang.msg === true)
            {
                decScoreCounter();
                updateViewScoreCounter();
                prompterData.tabooCard = objectFromErlang.newTabooCard;
                console.log("Ho fatto un errore. NUOVA CARTA: " + prompterData.tabooCard);
                updateViewTabooCard();
            }
            break;
        case "msgFromFriend":
            let divAreaGioco = document.getElementById("textChat");
            if(objectFromErlang.msg === "errorFromPrompter")
            {
                divAreaGioco.innerText = "";
                console.log("Il prompter ha sbagliato. Decremento ");
                decScoreCounter();
                updateViewScoreCounter();
            }
            else
            {   // ["mi", "chiamo", "come", "te"] --> dopo la join --> "mi chiamo come te"
                divAreaGioco.innerText = divAreaGioco.innerText + '\n' + (objectFromErlang.msg.join(' '));
                console.log("Message from friend: " + objectFromErlang.msg);
            }
            break;
        case "attemptGuessWord":
            if(myRole === "Guesser")
            {
                let divAreaGioco = document.getElementById("textChat");
                score += (objectFromErlang.msg === true) ? 1 : 0;
                updateViewScoreCounter();
                divAreaGioco.innerText = divAreaGioco.innerText + '\n' + ((objectFromErlang.msg === true) ? "Indovinato" : "Sbagliato");
                console.log("Il Prompter mi ha detto: " + ((objectFromErlang.msg === true) ? "Indovinato" : "Sbagliato"));
            }
            else
            {
                if(objectFromErlang.msg === true)
                {
                    score++;
                    updateViewScoreCounter();
                    prompterData.tabooCard = objectFromErlang.newTabooCard;
                    console.log("Hanno indovinato. Ricevuta nuova Carta: " + prompterData.tabooCard);
                    updateViewTabooCard();
                }
            }
            break;

        default:
            break;
    }
}

function onClickListenerBtnSendMsg()
{
    let genericMsg = document.getElementById("txtboxGenericMsg").value.toLowerCase();
    if(genericMsg === ""){
        alert("WARNING\nThe text-box is empty");
        return;
    }
    // Splittare genericMsg per spazio
    const genericMsgAsArray = genericMsg.split(' ');
    let actionGenericMsg =
        {
            action : "send_msg_to_friends",
            msg : genericMsgAsArray
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
    document.getElementById("txtboxGenericMsg").value = "";
}

function onClickListenerBtnPass()
{
    if(prompterData.passCounter >= 3)
        // sistemare il funzionamento del bottone
    {
        changeVisibilityBtn("pass-button",false);
        return;
    }
    prompterData.passCounter++;
    let passMsg = {
        action : "assignTabooCard"
    };
    socket.send(JSON.stringify(passMsg));
    if(prompterData.passCounter === 3)
        changeVisibilityBtn("pass-button",false);
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
    else
    {
        const posPrompter = match.rolesRivalTeam.findIndex(role => role === 'Prompter');
        if (posPrompter !== -1)
        {
            const newPosPrompter = (posPrompter + 1) % match.rolesRivalTeam.length;
            match.rolesRivalTeam[posPrompter] = 'Guesser';
            match.rolesRivalTeam[newPosPrompter] = 'Prompter';
        }
        const myPosInTeam = match.rivalTeam.findIndex(name => name === username);
        myRole = match.rolesRivalTeam[myPosInTeam];
    }

    sessionStorage.setItem("myRole", myRole);
    sessionStorage.setItem("match", JSON.stringify(match));

    prompterData.tabooCard = null;
    prompterData.passCounter = 0;

    changeVisibilityBtn("btnGuess",myRole === 'Guesser');
    changeVisibilityBtn("pass-button",myRole === 'Prompter');
    updateViewRole();

    stopCondition++;
    if (stopCondition === match.rolesInviterTeam.length)
    {
        //Stop The Game and insert match into MySQL DB
        if(myRole === "Prompter")
            addNewMatch();
        location.href = "../endGamePage.html";
    }
    document.getElementById("txtboxGenericMsg").value = "";
}

function addNewMatch()
{
    let matchJSON = sessionStorage.getItem("match");
    let match = JSON.parse(matchJSON);
    let myTeam = sessionStorage.getItem("myTeam");

    if(myTeam === "inviterTeam")
    {
        match.scoreInviterTeam = score;
        match.scoreRivalTeam = null;
    }
    else
    {
        match.scoreRivalTeam = score;
        match.scoreInviterTeam = null;
    }
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

function updateViewTabooCard()
{
    let wordToGuess = document.getElementById("wordToGuess");
    if(myRole === "Guesser")
    {
        wordToGuess.innerText = "Guess";
        for(let i = 1; i < 6; i++)
            document.getElementById("tabooWord" + i).innerText = "Guess";
        return;
    }

    wordToGuess.innerText = (prompterData.tabooCard[0][0].toUpperCase() + prompterData.tabooCard[0].slice(1));
    for(let i = 1; i < prompterData.tabooCard.length; i++)
        document.getElementById("tabooWord" + i).innerText = (prompterData.tabooCard[i][0].toUpperCase() + prompterData.tabooCard[i].slice(1));
}

function decScoreCounter()
{
    if(score === 0)
        return;
    score--;
}

function updateViewScoreCounter()
{
    document.getElementById("score").innerText = score;
}

function updateViewPassCounter()
{
    document.getElementById("p").innerText = score;
}
function updateViewRole()
{
    document.getElementById("usernameParagraph").innerText = username+",";
    document.getElementById("normalText").innerText =
        " in this turn you are " + ((myRole === "Prompter") ? "the " : "one of the ");
    document.getElementById("roleParagraph").innerText = myRole;
}

function loadTablesTeams()
// function that load both the tables, FriendTable and RivalTeam
{
    let matchJSON = sessionStorage.getItem("match");
    let match = JSON.parse(matchJSON);
    let i = 0;
    while(i < 2)
    {
        console.log("Caricamento tabella " + i+1);
        let workingTable = (i === 0) ? document.getElementById("tableFriend") : document.getElementById("tableRival");
        let friendList = (i === 0) ? match.inviterTeam : match.rivalTeam;
        emptyTable(workingTable);
        let usernameFriend;
        while( usernameFriend = friendList.pop() )
        {
            let trFriend= document.createElement("tr");
            trFriend.id = usernameFriend;

            let tdUserIcon = document.createElement("td");
            let imgUserIcon = document.createElement("img");
            imgUserIcon.className = "imgUserIcon";
            imgUserIcon.src = "../img/user_icon.png";
            imgUserIcon.alt = "user icon image";
            tdUserIcon.append(imgUserIcon);
            trFriend.append(tdUserIcon);

            let tdUsername = document.createElement("td");
            tdUsername.className = "tdUsername";
            tdUsername.id = usernameFriend;
            let pUsername = document.createElement("p");
            pUsername.innerText = usernameFriend;
            tdUsername.append(pUsername);
            trFriend.append(tdUsername);

            workingTable.append(trFriend);
        }
        i++;
    }
}

function emptyTable(table)
{
    while(table.childElementCount > 0)   // Delete all the old elements (if there are)
        table.removeChild(table.firstChild);
}

