const username = sessionStorage.getItem("userLog");
let showFriendList = true;

$(document).ready(function ()
{
    checkLogin();
    //ajaxGetFriendList();
    document.getElementById("btnShowSearchUser").onclick = function (e) { onClickListenerBtnShowSearchFunctions(); };
    document.getElementById("btnSearchUser").onclick = function (e) { onClickBtnSearchUser(e); };
    //document.getElementById("txtboxUserToSearch").addEventListener("keypress", function (event) { onClickBtnSearchUser(event); });
});

function checkLogin()
{
    if(!username)
    {
        //alert("You're not login");
        location.href = "../";
    }
}


function ajaxGetFriendList()
{
    //Fare una chiamata asincrona AJAX per ottenere la lista degli amici, ossia un JSON con una lista di nomi
    // e per ognuno, ci vede essere l'info se è Online o meno.
    // Per ogni amico, va creato a runTime un elemento HTML per mostrarlo.

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

function createFriendListInHtml(friendDTOList)
{
    let tableFriendBody = document.getElementById("friendListTableBody");
    //emptyFriendList(divContainer);
    while(friend = friendDTOList.pop())
    {
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
        tdUsername.id = friend.username;
        let pUsername = document.createElement("p");
        pUsername.innerText = friend.username;
        tdUsername.append(pUsername);
        trFriend.append(tdUsername);

        let tdStatus = document.createElement("td");
        tdStatus.className = "";
        let imgUserState = document.createElement("img");
        imgUserState.className = "imgUserState";
        imgUserState.src = (friend.logged) ? "../img/online.png" : "../img/offline.png";
        imgUserState.alt ="img user state (online or offline)";
        tdStatus.append(imgUserState);
        trFriend.append(tdStatus);

        let tdAction = document.createElement("td");
        tdAction.className = "";
        let btnRemoveFriend = document.createElement("button");
        btnRemoveFriend.className = "";
        btnRemoveFriend.id = "btnRemoveFriend&" + trFriend.id;
        btnRemoveFriend.innerText = "Remove Friend";
        btnRemoveFriend.onclick = function (e) { onClickListenerBtnRemoveFriends(this); };
        tdAction.append(btnRemoveFriend);
        trFriend.append(tdAction);

        tableFriendBody.append(trFriend);
    }
}

function emptyFriendList(divContainer)
{
    while(divContainer.childElementCount > 0)   // Delete all the old elemnt (if there are)
        divContainer.removeChild(divContainer.firstChild);
}

function onClickListenerBtnRemoveFriends(button)
{
    const username = button.id.toString().split('&');
    alert("Rimuovi amico -> " + username[1]);
}

function onClickListenerBtnShowSearchFunctions()
{
    let tBodySearchUser = document.getElementById("userListTableBody");
    let tBodyFriendList = document.getElementById("friendListTableBody");
    let txtBoxUserToSearch = document.getElementById("txtboxUserToSearch");
    let btnSearchUser = document.getElementById("btnShowSearchUser");

    if(showFriendList)
    {
        tBodyFriendList.className = "hidden"
        tBodySearchUser.className = "visible";
        txtBoxUserToSearch.className = "visible";
        btnSearchUser.className = "visible";
    }
    else
    {
        tBodyFriendList.className = "visible"
        tBodySearchUser.className = "hidden";
        txtBoxUserToSearch.className = "hidden";
        btnSearchUser.className = "visible";
        //ajaxGetFriendList();
    }
    showFriendList = !showFriendList;
}

function onClickBtnSearchUser(event)
{
    let usernameToSearch = document.getElementById("txtboxUserToSearch").value;
    document.getElementById("txtboxUserToSearch").value = "";
    let userSearchRequestDTO = {
        requesterUser : username,
        userToSearch : usernameToSearch
        };

    alert("userSearchRequestDTO: " + userSearchRequestDTO.requesterUser + ", " + userSearchRequestDTO.userToSearch);

    $.ajax({
        url: "http://localhost:8080/searchUser",
        type: "POST",
        data: JSON.stringify(userSearchRequestDTO),
        dataType: "json",
        contentType: 'application/json',
        success: function (serverResponse)
        {
            alert(serverResponse);

            let searchedUserList = serverResponse.responseMessage;
            let userToSearchDTO;
            if (searchedUserList)
            {
                alert("searchedUserList OK");
                //createFriendListInHtml(friendDTOList);
                while (userToSearchDTO = searchedUserList.pop())
                {
                    alert("User in list: [ " + userToSearchDTO.username + ", " + userToSearchDTO.name + ", " + userToSearchDTO.surname + "]");
                }

            }

        },
        error: function ()
        {
            alert("Unauthorized Request! ciao belòo");
            location.href = "../";
        }
    });
}