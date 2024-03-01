const username = sessionStorage.getItem("userLog");
let inviteFriendRequest;

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }

    if(!checkInviteRequest())
    {
        location.href = "../createTeamPage.html";
        return;
    }
    alert("CheckLogin & checkInvite: OK");
    ajaxGetFriendList();
    document.getElementById("btnInvite").onclick = function (e) { onClickListenerBtnInvite(); };
    document.getElementById("imgRefresh").onclick = function (e) { onClickImgRefresh(); };
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

function checkInviteRequest()
{
    if(!sessionStorage.getItem("inviteFriendRequest"))
    {
        alert("Create your team first!");
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
        imgUserIcon.src = "../img/user_icon.png";
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
        imgUserState.src = "../img/online.png";
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
    while(table.childElementCount > 0)   // Delete all the old elements (if there are)
        table.removeChild(table.firstChild);
}

function ajaxGetFriendList()
{
    $.ajax({
        url: "http://localhost:8080/getFriendList",
        type: "POST",
        data: username,
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let friendDTOList = serverResponse.responseMessage;
            if(friendDTOList)
                loadFriendsInTable(friendDTOList);
        },
        error: function ()
        {
            alert("Unauthorized Request!");
            location.href = "../";
        }
    });
}

function onClickListenerBtnInvite()
{
    const usernameRival = document.querySelector('input[name="rival"]:checked').id.toString().split('&')[1];
    inviteFriendRequest.userRival = usernameRival;

    $.ajax({
        url: "http://localhost:8080/inviteInTeam",
        type: "POST",
        data: JSON.stringify(inviteFriendRequest),
        contentType: 'application/json',
        success: function (serverResponse)
        {
            location.href = "../";
            alert("OK");
        },
        error: function () {
            alert("HTTP error");
            //location.href = "../";
        }
    });

}