
const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    checkLogin();
    setWelcomeText();
    document.getElementById('logoutBtn').onclick = function (e) { onClickListenerBtnLogout(); };
    document.getElementById('createTeamBtn').onclick = function (e) { onClickListenerBtnCreateTeam(); };
    document.getElementById('viewFriendsBtn').onclick = function (e) { onClickListenerBtnViewFriends(); };
});

function checkLogin()
{
    if(!username)
    {
        //alert("You're not login");
        location.href = "../";
    }
}

function setWelcomeText()
{
    let divWelcome = document.getElementById("h1WelcomeDiv");
    divWelcome.innerHTML = "Welcome "  + username + " to Your Home Page";
}

function onClickListenerBtnLogout()
{
    let username = sessionStorage.getItem("userLog");
    $.ajax({
        url: "http://localhost:8080/logout",
        type: "POST",
        data: username,
        dataType: "text",
        contentType: 'application/text',
        success: function ()
        {
            sessionStorage.removeItem("userLog");
            location.href = "../";
        },
        error: function (xhr)
        {
            let serverResponse = JSON.parse(xhr.responseText);
            alert(serverResponse.responseMessage);
            location.href = "../";
        }
    });
}

function onClickListenerBtnCreateTeam()
{
    //Contattare servlet per la creazione del team, oppure reindirizzare verso una createTeams.html
    //in cui selezionare da una lista di AMICI ONLINE quelli da inserire nella squadra (se è semplice, sarebbbe carino il drag and drop)
}

function onClickListenerBtnViewFriends()
{
    //Fare una chiamata asincrona AJAX per ottenere la lista degli amici, ossia un JSON con una lista di nomi
    // e per ognuno, ci vede essere l'info se è Online o meno.
    // Per ogni amico, va creato a runTime un elemento HTML per mostrarlo.

    location.href = "../friendListPage.html";
    return;
    $.ajax({
        url: "http://localhost:8080/getFriendList",
        type: "POST",
        data: username,
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let friendDTOList = serverResponse.responseMessage;
            if(friendDTOList)
            {
                createFriendListInHtml(friendDTOList);
            }
            else
                alert("friendList vuota");
        },
        error: function ()
        {
            alert("Unauthorized Request!");
            location.href = "../";
        }
    });
}

function emptyFriendList(divContainer)
{
    while(divContainer.childElementCount > 0)   // Delete all the old elemnt (if there are)
        divContainer.removeChild(divContainer.firstChild);
    divContainer.style.visibility = "hidden";
}

function createFriendListInHtml(friendDTOList)
{
    let divContainer = document.getElementById("friendListContainer");
    emptyFriendList(divContainer);
    while(friend = friendDTOList.pop())
    {
        let spanFriend = document.createElement("span");
        spanFriend.id = friend.username;
        spanFriend.className = "friend";

        let spanImgUserIcon = document.createElement("span");
        spanImgUserIcon.className = "listElement";
            let imgUserIcon = document.createElement("img");
            imgUserIcon.className = "imgUserIcon";
            imgUserIcon.src = "../img/user_icon.png";
            imgUserIcon.alt = "user icon image";
        spanImgUserIcon.append(imgUserIcon);

        let spanUsername = document.createElement("span");
        spanUsername.className = "listElement";
            let pUsername = document.createElement("p");
            pUsername.innerText = friend.username;
        spanUsername.append(pUsername);

        let spanImgUserState = document.createElement("span");
        spanImgUserState.className = "listElement";
            let imgUserState = document.createElement("img");
            imgUserState.className = "imgUserState";
            imgUserState.src = (friend.logged) ? "../img/online.png" : "../img/offline.png";
            imgUserState.alt ="img user state (online or offline)";
        spanImgUserState.append(imgUserState);

        spanFriend.append(spanImgUserIcon);
        spanFriend.append(spanUsername);
        spanFriend.append(spanImgUserState);

        divContainer.append(spanFriend);
        divContainer.style.visibility = "visible";
    }
    if(divContainer.childElementCount > 0)
        divContainer.style.height = "400px";
}