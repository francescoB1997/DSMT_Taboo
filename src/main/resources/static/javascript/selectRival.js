const username = sessionStorage.getItem("userLog");

let inviteFriendRequest;

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "./";
        return;
    }

    if(!checkInviteInSessionStorage())
    {
        location.href = "./createTeamPage.html";
        return;
    }

    ajaxGetFriendList();
    document.getElementById("btnInvite").onclick = function (e) { onClickListenerBtnInvite(); };
    document.getElementById("imgRefresh").onclick = function (e) { onClickImgRefresh(); };
});

function checkLogin()
{
    if(!username)
    {
        alert("You're Not Logged");
        return false;
    }
    return true;
}

function checkInviteInSessionStorage()
{
    if(!sessionStorage.getItem("inviteFriendRequest"))
    {
        alert("Create Your Team First!");
        return false;
    }
    inviteFriendRequest = JSON.parse(sessionStorage.getItem("inviteFriendRequest"));
    return true;
}

function onClickImgRefresh()
{
    ajaxGetFriendList();
}

function loadFriendsInTable(friendList)
{
    let tableFriends = document.getElementById("tableFriend");
    emptyTable(tableFriends);
    let friend;
    while( friend = friendList.pop() )
    {
        if(!friend.logged)
            continue;

        if(userIsInTeam(friend.username))
            continue;

        let trFriend= document.createElement("tr");
        trFriend.id = friend.username;

        let tdUserIcon = document.createElement("td");
        let imgUserIcon = document.createElement("img");
        imgUserIcon.className = "imgUserIcon";
        imgUserIcon.src = "./img/user_icon.png";
        imgUserIcon.alt = "user icon image";
        tdUserIcon.append(imgUserIcon);
        trFriend.append(tdUserIcon);

        let tdUsername = document.createElement("td");
        tdUsername.className = "tdUsername";
        tdUsername.id = friend.username;
        let pUsername = document.createElement("p");
        pUsername.innerText = friend.username;
        tdUsername.append(pUsername);
        trFriend.append(tdUsername);

        let tdStatus = document.createElement("td");
        tdStatus.className = "";
        let imgUserState = document.createElement("img");
        imgUserState.className = "img";
        imgUserState.src = "./img/online.png";
        imgUserState.alt = "img user state (online or offline)";
        tdStatus.append(imgUserState);
        trFriend.append(tdStatus);

        let tdRadio = document.createElement("td");
        let lblRadioRival = document.createElement("label");
        lblRadioRival.innerText = "Choose as Rival";
        let radioRival = document.createElement("input");
        radioRival.id = "radio&" + friend.username;
        radioRival.setAttribute("type", "radio");
        radioRival.setAttribute("name", "rival");
        radioRival.setAttribute("value", radioRival.id);
        lblRadioRival.htmlFor = radioRival.id;

        tdRadio.append(radioRival);
        tdRadio.append(lblRadioRival);
        trFriend.append(tdRadio);

        tableFriends.append(trFriend);
    }
}

function userIsInTeam(username)
{
    for(const userInTeam of inviteFriendRequest.yourTeam)
        if(username === userInTeam)
            return true;
    return false;
}

function emptyTable(table)
{
    while(table.childElementCount > 0)   //if present, delete the old elements
        table.removeChild(table.firstChild);
}

function ajaxGetFriendList()
{
    $.ajax({
        url: "./getFriendList",
        type: "POST",
        data: username,
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let friendDTOList = serverResponse.responseMessage;
            if(friendDTOList)
                loadFriendsInTable(friendDTOList);
        },
        error: function (xhr)
        {
            if(xhr.status === 400)
                alert("Service Temporary Unavailable");
            else
            {
                alert("Unauthorized Request!");
                location.href = "./";
            }
        }
    });
}

function onClickListenerBtnInvite()
{
    const radioSelected = document.querySelector('input[name="rival"]:checked');
    if(radioSelected === null || radioSelected === undefined)
    {
        alert("You Must Choose Your RIVAL");
        return;
    }
    const usernameRival = radioSelected.id.toString().split('&')[1];

    inviteFriendRequest.rivals.push(usernameRival);
    sessionStorage.setItem("myRole", inviteFriendRequest.roles[0]);
    $.ajax({
        url: "./inviteFriends",
        type: "POST",
        data: JSON.stringify(inviteFriendRequest),
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let gameId = serverResponse.responseMessage;
            storeInvitation(true, gameId, true);
        },
        error: function () {
            alert("Unauthorized Request!");
            location.href = "./";
        }
    });
    sessionStorage.removeItem("inviteFriendRequest");
}

function storeInvitation(accepted, inviteId, invitedAsFriend)
{
    let inviteReply =
        {
            senderUsername: username,
            gameId: inviteId,
            inviteState: accepted,
            invitedAsFriend: invitedAsFriend
        };
    sessionStorage.setItem("inviteReply", JSON.stringify(inviteReply));
    location.href = "./waitingPage.html";
}