const username = sessionStorage.getItem("userLog");
let checkedCheckbox = [];

$(document).ready(function ()
{
    checkLogin();
    ajaxGetFriendList();
    document.getElementById("btnInvite").onclick = function (e) { onClickListenerBtnInvite(); };
    document.getElementById("imgRefresh").onclick = function (e) { onClickImgRefresh(); };
});

function checkLogin()
{
    if(!username)
    {
        alert("You're not logged");
        location.href = "../";
    }
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
    let inviteInTeamRequest =
    {
        gameId : "",
        yourTeam : [],
        roles: [],
        userInviter : username
    };
    let checkboxes = document.querySelectorAll("input[type='checkbox']:checked");
    inviteInTeamRequest.yourTeam.push(username); // Push my username first

    for (let i = 0 ; i < checkboxes.length; i++)
        inviteInTeamRequest.yourTeam.push(checkboxes[i].id.toString().split('&')[1]);

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
    inviteInTeamRequest.roles.push(myRole); // Push my role first
    for(let checkbox of checkboxes) // foreach checked friend, fill the roles array
    {
        inviteInTeamRequest.roles.push("Guesser");
    }
    if(myRole !== "Prompter")// if me is not the Prompter, then it have to be randomly chosen from my friends
    {
        let maxIndex = checkboxes.length;
        let randomPositionPrompter = getRandomInt(0, maxIndex);
        inviteInTeamRequest.roles[randomPositionPrompter] = "Prompter";
    }
    alert("Array: " + inviteInTeamRequest.roles);

    $.ajax({
        url: "http://localhost:8080/inviteInTeam",
        type: "POST",
        data: JSON.stringify(inviteInTeamRequest),
        contentType: 'application/json',
        success: function (serverResponse)
        {
            alert("OK")
        },
        error: function ()
        {
            alert("HTTP error");
            //location.href = "../";
        }
    });

}

function getRandomInt(min , max)
{
    let randomInt = 0;
    while(randomInt === 0) {
        min = Math.ceil(min);
        max = Math.floor(max);
        randomInt = Math.floor(Math.random() * (max - min + 1)) + min;
    }
    return randomInt;
}