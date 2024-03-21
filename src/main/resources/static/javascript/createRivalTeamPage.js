const username = sessionStorage.getItem("userLog");
let invite = null;
let checkedCheckbox = [];

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }
    if(!sessionStorage.getItem("invite"))
    {
        alert("invite object non è in SessionStorage -> Non sei stato invitato ma vuoi fare il furbo!");
        location.href = "../";
        return;
    }
    invite = JSON.parse(sessionStorage.getItem("invite"));
    ajaxGetFriendList();
    loadInviterTeam();
    document.getElementById("btnInvite").onclick = function (e) { onClickListenerBtnInvite(); };
    document.getElementById("imgRefresh").onclick = function (e) { onClickImgRefresh(); };
    sessionStorage.removeItem("inviteFriendRequest");
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

function onClickImgRefresh()
{
    ajaxGetFriendList();
}

function onClickListenerBtnInvite()
{
    let checkboxes = document.querySelectorAll("input[type='checkbox']:checked");

    chooseMyRole(checkboxes);

    //alert("Rivals: " + invite.rivals);
    $.ajax({
        url: "http://localhost:8080/inviteFriends",
        type: "POST",
        data: JSON.stringify(invite),
        contentType: 'application/json',
        success: function (serverResponse)
        {
            //alert("OK -> Rivali ricevuti -> Rendirizzamento a Game-Attesa");
            storeInvitation(true, invite.gameId, false);
        },
        error: function ()
        {
            alert("HTTP error");
            location.href = "../";
        }
    });

}

function chooseMyRole(checkboxes)
{
    for (let i = 0; i < checkboxes.length; i++)
        invite.rivals.push(checkboxes[i].id.toString().split('&')[1]);

    let myRole;
    let radioList = document.querySelectorAll('input[name="myRole"]');
    for(const radio of radioList)
    {
        if(radio.checked)
        {
            myRole = radio.value;
            break;
        }
    }
    inviteFriendRequest.roles.push(myRole); // Push my role first
    for(let checkbox of checkboxes) // foreach checked friend, fill the roles array
    {
        inviteFriendRequest.roles.push("Guesser");
    }

    if(myRole !== "Prompter")// if me is not the Prompter, then it have to be randomly chosen from my friends
    {
        let maxIndex = checkboxes.length;
        let randomPositionPrompter = getRandomInt(0, maxIndex);
        inviteFriendRequest.roles[randomPositionPrompter] = "Prompter";
    }

    //alert("Array: " + inviteFriendRequest.roles);
    sessionStorage.setItem("inviteFriendRequest", JSON.stringify(inviteFriendRequest));
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
    location.href = "../waitingPage.html";
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
                loadRivalFriendsInTable(friendDTOList);
        },
        error: function ()
        {
            alert("Unauthorized Request!");
            location.href = "../";
        }
    });
}

function loadRivalFriendsInTable(friendList)
{
    let tableFriends = document.getElementById("tableFriend");
    emptyTable(tableFriends);
    let friend;
    while( friend = friendList.pop() )
    {
        if(!friend.logged)
            continue;

        if(userAlreadyInTeam(friend.username)) // If true -> I'm hiding the username that are already selected in the InviterTeam
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

        let tdCheckbox = document.createElement("td");
        let lblCheckboxFriend = document.createElement("label");
        lblCheckboxFriend.innerText = "Add in your Team ";
        let checkboxFriend = document.createElement("input");
        checkboxFriend.id = "check&" + friend.username;
        lblCheckboxFriend.htmlFor = checkboxFriend.id;
        checkboxFriend.setAttribute("type", "checkbox");

        tdCheckbox.append(checkboxFriend);
        tdCheckbox.append(lblCheckboxFriend);
        trFriend.append(tdCheckbox);

        tableFriends.append(trFriend);
    }
}

function emptyTable(table)
{
    while(table.childElementCount > 0)   // Delete all the old elements (if there are)
        table.removeChild(table.firstChild);
}

function userAlreadyInTeam(username)
{
    for(const userInTeam of invite.yourTeam)
        if(username === userInTeam)
            return true;
    return false;
}

function loadInviterTeam()
{
    let inviterFriendsTable = document.getElementById("inviterFriendsTable");
    emptyTable(inviterFriendsTable);
    let inviterFriend;
    invite.yourTeam.forEach((inviterFriend) =>
    {
        let trInviterFriend= document.createElement("tr");
        trInviterFriend.id = inviterFriend;

        let tdUserIcon = document.createElement("td");
        let imgUserIcon = document.createElement("img");
        imgUserIcon.className = "imgUserIcon";
        imgUserIcon.src = "../img/user_icon.png";
        imgUserIcon.alt = "user icon image";
        tdUserIcon.append(imgUserIcon);
        trInviterFriend.append(tdUserIcon);

        let tdUsername = document.createElement("td");
        tdUsername.className = "tdUsername";
        tdUsername.id = inviterFriend;
        let pUsername = document.createElement("p");
        pUsername.innerText = inviterFriend;
        tdUsername.append(pUsername);
        trInviterFriend.append(tdUsername);

        let tdStatus = document.createElement("td");
        tdStatus.className = "";
        let imgUserState = document.createElement("img");
        imgUserState.className = "img";
        imgUserState.src = "../img/online.png";
        imgUserState.alt = "img user state (online or offline)";
        tdStatus.append(imgUserState);
        trInviterFriend.append(tdStatus);

        inviterFriendsTable.append(trInviterFriend);
    })
}