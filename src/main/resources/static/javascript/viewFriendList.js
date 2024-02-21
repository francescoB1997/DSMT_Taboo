const username = sessionStorage.getItem("userLog");
let showFriendList = true;

$(document).ready(function ()
{
    checkLogin();
    ajaxGetFriendList();
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
    let tableFriendList = document.getElementById("friendListTable");
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

        tableFriendList.append(trFriend);
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
    let searchedUserTable = document.getElementById("searchedUserListTable");
    let friendListTable = document.getElementById("friendListTable");
    let btnSearchUser = document.getElementById("btnSearchUser");

    if(showFriendList)
    {
        friendListTable.classList.remove("visible");
        friendListTable.classList.add("hidden");
        searchedUserTable.classList.remove("hidden");
        searchedUserTable.classList.add("visible");
        btnSearchUser.classList.remove("hidden");
        btnSearchUser.classList.add("visible");
    }
    else
    {
        friendListTable.classList.remove("hidden");
        friendListTable.classList.add("visible");
        searchedUserTable.classList.remove("visible");
        searchedUserTable.classList.add("hidden");
        btnSearchUser.classList.remove("visible");
        btnSearchUser.classList.add("hidden");
        //ajaxGetFriendList();
    }
    showFriendList = !showFriendList;
}

function onClickBtnSearchUser(event)
{
    let usernameToSearch = document.getElementById("txtboxUserToSearch").value;
    document.getElementById("txtboxUserToSearch").value = "";
    let userSearchRequestDTO = {
        requesterUsername : username,
        usernameToSearch : usernameToSearch
        };

    alert("userSearchRequestDTO: " + userSearchRequestDTO.requesterUsername + ", " + userSearchRequestDTO.usernameToSearch);
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
                createTableSearchedUserInHtml(searchedUserList);
            }

        },
        error: function ()
        {
            alert("Unauthorized Request! ciao belòo");
            location.href = "../";
        }
    });
}

function createTableSearchedUserInHtml(searchedUserList)
{
    let searchedUserTable = document.getElementById("searchedUserListTable");
    while(user = searchedUserList.pop())
    {
        let trUser = document.createElement("tr");
        trUser.id = user.username;

        let tdUserIcon = document.createElement("td");
        let imgUserIcon = document.createElement("img");
        imgUserIcon.className = "imgUserIcon";
        imgUserIcon.src = "../img/user_icon.png";
        imgUserIcon.alt = "user icon image";
        tdUserIcon.append(imgUserIcon);
        trUser.append(tdUserIcon);

        let tdUsername = document.createElement("td");
        tdUsername.id = user.username;
        let pUsername = document.createElement("p");
        pUsername.innerText = user.username;
        tdUsername.append(pUsername);
        trUser.append(tdUsername);

        let tdName = document.createElement("td");
        tdName.className = "";
        let pName = document.createElement("p");
        pName.innerText = user.name;
        tdName.append(pName);
        trUser.append(tdName);

        let tdSurname = document.createElement("td");
        tdSurname.className = "";
        let pSurname = document.createElement("p");
        pSurname.innerText = user.surname;
        tdSurname.append(pSurname);
        trUser.append(tdSurname);

        let tdAction = document.createElement("td");
        tdAction.className = "";
        let btnAddFriend = document.createElement("button");
        btnAddFriend.className = "";
        btnAddFriend.id = "btnAddFriend&" + trUser.id;
        btnAddFriend.innerText = "Add Friend";
        btnAddFriend.onclick = function (e) { onClickListenerBtnAddFriends(this); };
        tdAction.append(btnAddFriend);
        trUser.append(tdAction);

        searchedUserTable.append(trUser);
    }
}

function onClickListenerBtnAddFriends(button)
{
    const username = button.id.toString().split('&');
    alert("Aggiunto amico -> " + username[1]);
}