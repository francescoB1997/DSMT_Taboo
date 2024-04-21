const IP_Server_ERLANG = "10.2.1.110:8090";
const GAME_DURATION = 60;
const PASS = 3;

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

    if (!checkLogin())
    {
        location.href = "./";
        return;
    }

    setInterval(keepAlive, 20000);

    if(myRole === "Guesser")
        updateViewTabooCard();
    else
        updateViewPassCounter();

    sessionStorage.setItem("myPrompterName", extractMyPrompterName());
    updateViewRole();
    updateViewPassCounter();
    setWelcomeText();

    changeVisibilityBtn("btnGuess",myRole === 'Guesser');
    changeVisibilityBtn("pass-button",myRole === 'Prompter');

    document.getElementById("txtboxGenericMsg").addEventListener("keypress", handlerEnterKeyPress);

    initAndConfigureSocket(undefined);

    document.getElementById("timer" ).innerText = GAME_DURATION;


    document.getElementById("btnSendMsg").onclick = function (e ) { onClickListenerBtnSendMsg(); }
    document.getElementById("btnGuess").onclick = function (e) { onClickListenerBtnGuess(); }
    document.getElementById("pass-button").onclick = function (e) { onClickListenerBtnPass(); }

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
        alert("You're Not Logged");
        return false;
    }
    if( sessionStorage.getItem("match") === "" ||
        sessionStorage.getItem("match") === undefined ||
        sessionStorage.getItem("match") === null )
    {
        alert("No Match");
        return false;
    }
    return true;
}

function handlerEnterKeyPress(event)
{
    if (event.key === "Enter") {
        event.preventDefault(); //To avoid submission of the form (if any).
        onClickListenerBtnSendMsg();
    }
}

function initAndConfigureSocket(event)
{
    socket = new WebSocket("ws://" + IP_Server_ERLANG + "/erlServer");
    socket.addEventListener("open", (event) => { sendInitMsg();});
    socket.addEventListener("message", (event) => { msgOnSocketRecevedListener(event);});

    socket.addEventListener("close", (event) => {
        //console.log("socket chiuso, riapro");
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

        stopCondition++;
        const match = JSON.parse(sessionStorage.getItem("match"));
        //console.log("StopCond: " + stopCondition + " | rolesInviterTeamLenght : " + match.rivalTeam.length);
        if (stopCondition === match.rivalTeam.length)
        {
            //Stop The Game and insert match into MySQL DB, made only by the prompter
            if(myRole === "Prompter")
                addNewMatch();
            else
                location.href = "./endGamePage.html";
            return;
        }
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
    const myPrompterName = sessionStorage.getItem("myPrompterName");
    let matchJSON = sessionStorage.getItem("match");
    let match = JSON.parse(matchJSON);

    loadTablesTeams();

    let friendList = extractMyTeam(match);
    if (friendList != null)
    {
        let loginMsg = {
            action: "login",
            username: username,
            prompterName : myPrompterName,
            friendList: friendList,
            role: myRole
        };
        socket.send(JSON.stringify(loginMsg));
    }
}

function msgOnSocketRecevedListener (event)
{
    //alert("Server Sent me: " + event.data);
    objectFromErlang = JSON.parse(event.data);
    switch(objectFromErlang.action)
    {
        case "loginOk":
            if(myRole === "Prompter")
            {
                let assignTabooCardMsg = { action : "assignTabooCard" };
                //console.log("Sent taboocard, wait 2 seconds and send start");
                socket.send(JSON.stringify(assignTabooCardMsg));

                setTimeout(function() {
                   // console.log("start sent");
                    let startGameMsg = { action : "startGame"};
                    socket.send(JSON.stringify(startGameMsg));
                    clearInterval(timerInterval);
                    timerInterval = setInterval(timerHandler, 1000);
                },2000);
            }
            break;
        case "wakeUpGuesser":
            if(myRole === "Guesser")
            {
                //console.log("I am a Guesser, the game can start");
                clearInterval(timerInterval);
                timerInterval = setInterval(timerHandler, 1000);
            }
            break;
        case "tabooCard":
            prompterData.tabooCard = objectFromErlang.msg;
            updateViewTabooCard();
            //if(myRole === "Prompter")
                //console.log("TabooCard Received: " + prompterData.tabooCard);
            break;
        case "checkWordResult":
            if (objectFromErlang.msg === true)
            {
                decScoreCounter();
                updateViewScoreCounter();
                prompterData.tabooCard = objectFromErlang.newTabooCard;
                //console.log("(Prompter) I got it Wrong. I'll receive a NEW CARD.: " + prompterData.tabooCard);
                updateViewTabooCard();
            }
            break;
        case "msgFromFriend":
            let divAreaGioco = document.getElementById("textChat");
            if(objectFromErlang.msg === "errorFromPrompter")
            {
                //divAreaGioco.innerText = "";
                //console.log("Il prompter ha sbagliato. Decremento ");
                decScoreCounter();
                updateViewScoreCounter();
            }
            else
            {
                divAreaGioco.innerText = divAreaGioco.innerText + '\n' + (objectFromErlang.msg.join(' '));
                //console.log("Message from friend: " + objectFromErlang.msg);
            }
            break;
        case "attemptGuessWord":
            if(myRole === "Guesser")
            {
                let divAreaGioco = document.getElementById("textChat");
                score += (objectFromErlang.msg === true) ? 1 : 0;
                updateViewScoreCounter();
                divAreaGioco.innerText = divAreaGioco.innerText + '\n' + ((objectFromErlang.msg === true) ? "*** Well Done! Guessed it ***" : "*** Wrong! Try to Guess Again ***");
                //console.log("Prompter told me: " + ((objectFromErlang.msg === true) ? "*** Well Done! Guessed it ***" : "*** Wrong! Try to Guess Again ***"));
            }
            else
            {
                if(objectFromErlang.msg === true)
                {
                    score++;
                    updateViewScoreCounter();
                    prompterData.tabooCard = objectFromErlang.newTabooCard;
                    //console.log("Have Guessed. New Taboo-Card Received: " + prompterData.tabooCard);
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
    let genericMsg = document.getElementById("txtboxGenericMsg").value;
    if(genericMsg === ""){
        //alert("WARNING\nThe text-box is empty");
        return;
    }
    document.getElementById("textChat").innerText += "\n" + genericMsg;
    const genericMsgLowerCase = genericMsg.toLowerCase();

    const genericMsgAsArray = genericMsgLowerCase.split(' '); //genericMsg Split by space
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
    if(attemptedWord === "")
    {
        alert("WARNING!\nThe text-box is empty");
        return;
    }

    if(attemptedWord.includes(" "))
    {
        document.getElementById("txtboxGenericMsg").value = "";
        alert("WARNING\nYou Can Only Send 1 Word to Guess");
        return;
    }

    let actionGuess =
        {
            action : "attemptGuessWord",
            word : attemptedWord.toLowerCase()
        };
    socket.send(JSON.stringify(actionGuess));
    document.getElementById("txtboxGenericMsg").value = "";
}

function onClickListenerBtnPass()
{
    if(prompterData.passCounter >= 3)
    {
        changeVisibilityBtn("pass-button",false);
        return;
    }
    prompterData.passCounter++;
    updateViewPassCounter();
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
    sessionStorage.setItem("myPrompterName", extractMyPrompterName());

    prompterData.tabooCard = null;
    prompterData.passCounter = 0;

    if(myRole === "Guesser")
        updateViewTabooCard();

    changeVisibilityBtn("btnGuess",myRole === 'Guesser');
    changeVisibilityBtn("pass-button",myRole === 'Prompter');
    updateViewRole();

    document.getElementById("txtboxGenericMsg").value = "";
    document.getElementById("textChat").innerText = "";

}

function addNewMatch()
{
    let match = JSON.parse(sessionStorage.getItem("match"));
    let myTeam = sessionStorage.getItem("myTeam");

    let matchResultRequest = {
        matchId : match.matchId,
        usernameRequester : username,
        scoreInviterTeam : (myTeam === "inviterTeam") ? score : null,
        scoreRivalTeam : (myTeam === "rivalTeam") ? score : null
    };

    $.ajax({
        url : "./addNewMatch",
        type : "POST",
        data : JSON.stringify(matchResultRequest),
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let addMatchServerResult = serverResponse.responseMessage;
            switch (addMatchServerResult)
            {
                case 1:
                    location.href = "./endGamePage.html";
                    //console.log("The match has been successfully added into DB.");
                    break;
                case -1:
                    console.log("We're Sorry, an Error occurred during adding operation." +
                       " The match has not been successfully added into DB");
                    location.href = "./loggedPlayerPage.html";
                    break;
                case -2:
                    console.log("il server non ha il mio match. Forse non ce l'avevo nemmeno io");
                    location.href = "./loggedPlayerPage.html";
                    break;
                default:
                    break;
            }
        },
        error: function (xhr)
        {
            if(xhr.status === 400)
                alert("Service temporary unavailable");
            else
            {
                alert("Unauthorized Request!");
                location.href = "./";
            }
        }
    });

}

function extractMyPrompterName()
{
    //const match = (passedMatch === null) ? JSON.parse(sessionStorage.getItem("match")) : passedMatch;
    const match = JSON.parse(sessionStorage.getItem("match"));
    extractMyTeam(match);
    const myTeam = sessionStorage.getItem("myTeam");
    let myPrompterName = null;
    if(myTeam === "inviterTeam")
    {
        const posPrompter = match.rolesInviterTeam.findIndex(role => role === 'Prompter');
        myPrompterName = match.inviterTeam[posPrompter];
    }
    else if(myTeam === "rivalTeam")
    {
        const posPrompter = match.rolesRivalTeam.findIndex(role => role === 'Prompter');
        myPrompterName = match.rivalTeam[posPrompter];
    }
    return myPrompterName;
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
    document.getElementById("pass-card").innerText = PASS - prompterData.passCounter;
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
        //console.log("Caricamento tabella " + i+1);
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
            imgUserIcon.src = "./img/user_icon.png";
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