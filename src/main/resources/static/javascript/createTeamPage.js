const username = sessionStorage.getItem("userLog");
const PERIOD = 5000;

$(document).ready(function ()
{
    checkLogin();
    ajaxGetFriendList();
    setPeriodicRequest(PERIOD);
});

function checkLogin()
{
    if(!username)
    {
        alert("You're not logged");
        location.href = "../";
    }
}

function setPeriodicRequest(period)
{
    let intervalId = setInterval(function () {
        ajaxGetFriendList();
    }, period);
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
        let trFriend = document.createElement("tr");
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
        lblCheckboxFriend.innerText = "Select as Friend";
        let checkboxFriend = document.createElement("input");
        checkboxFriend.id = "check&" + friend.username;
        lblCheckboxFriend.htmlFor = checkboxFriend.id;
        checkboxFriend.setAttribute("type", "checkbox");

        tdCheckbox.append(lblCheckboxFriend);
        tdCheckbox.append(checkboxFriend);
        trFriend.append(tdCheckbox);

        tableFriends.append(trFriend);
    }
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
            {
                loadFriendsInTable(friendDTOList);
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