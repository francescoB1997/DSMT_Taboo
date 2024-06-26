const username = sessionStorage.getItem("userLog");

let showFriendList = true;

$(document).ready(function ()
{

    if(!checkLogin())
    {
        location.href = "./";
        return;
    }

    ajaxGetFriendList();
    document.getElementById("btnShowSearchUser").onclick = function (e) { onClickListenerBtnShowSearchFunctions(); };
    document.getElementById("btnSearchUser").onclick = function (e) { onClickBtnSearchUser(e); };
    document.getElementById("txtboxUserToSearch").addEventListener("keypress", handlerEnterKeyPress);
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

function handlerEnterKeyPress(event)
{
    if (event.key === "Enter") {
        event.preventDefault(); //To avoid submission of the form (if any).
        onClickBtnSearchUser(event);
    }
}

/* -------------------------- Friend User Management -------------------------- */

function onClickListenerBtnShowSearchFunctions()
{
    let searchedUserTable = document.getElementById("searchedUserListTable");
    let friendListTable = document.getElementById("friendListTable");
    let btnSearchUser = document.getElementById("btnSearchUser");
    let btnShowSearchUser = document.getElementById("btnShowSearchUser");
    let txtboxUserToSearch = document.getElementById("txtboxUserToSearch");

    if(showFriendList)
    {
        friendListTable.classList.remove("visible");
        friendListTable.classList.add("hidden");
        searchedUserTable.classList.remove("hidden");
        searchedUserTable.classList.add("visible");
        btnSearchUser.classList.remove("hidden");
        btnSearchUser.classList.add("visible");
        txtboxUserToSearch.classList.remove("hidden");
        txtboxUserToSearch.classList.add("visible");
        btnShowSearchUser.innerText = "Back To Friend List";
    }
    else
    {
        friendListTable.classList.remove("hidden");
        friendListTable.classList.add("visible");
        searchedUserTable.classList.remove("visible");
        searchedUserTable.classList.add("hidden");
        btnSearchUser.classList.remove("visible");
        btnSearchUser.classList.add("hidden");
        txtboxUserToSearch.classList.remove("visible");
        txtboxUserToSearch.classList.add("hidden");
        btnShowSearchUser.innerText = "Search New Friends";
        ajaxGetFriendList();
        emptyTable(searchedUserTable);
    }
    showFriendList = !showFriendList;
}

function ajaxGetFriendList()
{
    //An asynchronous AJAX call to get the list of friends, a JSON with a list of names
    // And for each one, there sees to be info if it is Online or not.
    // For each friend, an HTML element must be created at runTime to show it.

    $.ajax({
        url: "./getFriendList",
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
                alert("Friend-List Empty");
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


function createFriendListInHtml(friendDTOList)
{
    let tableFriendList = document.getElementById("friendListTable");
    emptyTable(tableFriendList);
    while(friend = friendDTOList.pop())
    {
        let trFriend = document.createElement("tr");
        trFriend.id = friend.username;

        let tdUserIcon = document.createElement("td");
        let imgUserIcon = document.createElement("img");
        imgUserIcon.className = "imgUserIcon";
        imgUserIcon.src = "./img/user_icon.png";
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
        imgUserState.src = (friend.logged) ? "./img/online.png" : "./img/offline.png";
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

function emptyTable(table)
{
    while(table.childElementCount > 0)  //if present, delete the old elements
        table.removeChild(table.firstChild);
}

function onClickListenerBtnRemoveFriends(button)
{
    const usernameToRemove = button.id.toString().split('&')[1];
    if (confirm("Are you sure you want to Remove " + usernameToRemove + " from your Friends?")) {

        let removeFriendRequest = {
            username : username,
            usernameFriend : usernameToRemove
        };

        $.ajax({
            url: "./removeFriend",
            type: "POST",
            data:  JSON.stringify(removeFriendRequest),
            contentType: 'application/json',
            success: function (serverResponse)
            {
                let removeOperation = serverResponse.responseMessage;
                switch (removeOperation)
                {
                    case 0:
                        alert("The User " + usernameToRemove + " has been successfully removed.");
                        ajaxGetFriendList();
                        break;
                    case 1:
                        alert("We're Sorry, an Error occurred during remove operation." +
                            " The friend " + usernameToRemove + " has NOT been removed from your friend list");
                        break;
                    default:
                        //alert("Default: " + responseMessage);
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
}

/* -------------------------- Global User Management -------------------------- */

function onClickBtnSearchUser(event)
{
    let usernameToSearch = document.getElementById("txtboxUserToSearch").value;
    if (usernameToSearch === "") {
        alert("Empty Field!");
        return;
    }
    document.getElementById("txtboxUserToSearch").value = "";
    let userSearchRequestDTO = {
        requesterUsername : username,
        usernameToSearch : usernameToSearch
    };

    $.ajax({
        url: "./searchUser",
        type: "POST",
        data: JSON.stringify(userSearchRequestDTO),
        dataType: "json",
        contentType: 'application/json',
        success: function (serverResponse)
        {
            //alert(serverResponse);
            let searchedUserList = serverResponse.responseMessage;
            if (searchedUserList)
            {
                //alert("- OK - : The searched user is present in the Database");
                createTableSearchedUserInHtml(searchedUserList);
            }
            else {
                alert("- NOT FOUND - : The user  [" + usernameToSearch + "] was not found into db");
            }
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

function createTableSearchedUserInHtml(searchedUserList)
{
    let searchedUserTable = document.getElementById("searchedUserListTable");
    emptyTable(searchedUserTable);

    while(user = searchedUserList.pop())
    {
        let trUser = document.createElement("tr");
        trUser.id = user.username;

        let tdUserIcon = document.createElement("td");
        let imgUserIcon = document.createElement("img");
        imgUserIcon.className = "imgUserIcon";
        imgUserIcon.src = "./img/user_icon.png";
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
        if(btnAddFriend.id === ("btnAddFriend&" + username))
            btnAddFriend.disabled = true;
        else
            btnAddFriend.onclick = function (e) { onClickListenerBtnAddFriends(this); };
        tdAction.append(btnAddFriend);
        trUser.append(tdAction);

        searchedUserTable.append(trUser);
    }
}

function onClickListenerBtnAddFriends(button)
{
    const usernameFriendToAdd = button.id.toString().split('&')[1];

    let addFriendRequest = {
        username : username,
        usernameFriend : usernameFriendToAdd
    };
    $.ajax({
        url: "./addFriend",
        type: "POST",
        data: JSON.stringify(addFriendRequest),
        dataType: "json",
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let responseMessage= serverResponse.responseMessage;
            switch (responseMessage)
            {
                case 0:
                    alert("You and this User are already Friends");
                    break;
                case 1:
                    alert("Friend Successfully Added into your Friend-List ");
                    break;
                default:
                    alert("Default: " + responseMessage);
                    break;
            }
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