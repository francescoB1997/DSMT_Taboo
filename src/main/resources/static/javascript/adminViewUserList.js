const username = sessionStorage.getItem("userLog");
let showUsersList = true;

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }

    ajaxGetUserList();
    document.getElementById("btnShowSearchUser").onclick = function (e) { onClickListenerBtnShowSearchFunctions(); };
    document.getElementById("btnSearchUser").onclick = function (e) { onClickBtnSearchUser(e); };
    document.getElementById("txtboxUserToSearch").addEventListener("keypress", handlerEnterKeyPress);
});

function checkLogin()
{
    if(!username || (username !== "admin"))
    {
        //alert("You're not logged as ADMIN");
        return false;
    }
    return true;
}

function handlerEnterKeyPress(event)
{
    if (event.key === "Enter") {
        event.preventDefault(); // Per evitare l'invio del modulo (se presente)
        onClickBtnSearchUser(event);
    }
}

/* -------------------------- User Management -------------------------- */

function onClickListenerBtnShowSearchFunctions()
{
    let searchedUserTable = document.getElementById("searchedUserListTable");
    let usersListTable = document.getElementById("usersListTable");
    let btnSearchUser = document.getElementById("btnSearchUser");
    let btnShowSearchUser = document.getElementById("btnShowSearchUser");
    let txtboxUserToSearch = document.getElementById("txtboxUserToSearch");

    if(showUsersList)
    {
        usersListTable.classList.remove("visible");
        usersListTable.classList.add("hidden");
        searchedUserTable.classList.remove("hidden");
        searchedUserTable.classList.add("visible");
        btnSearchUser.classList.remove("hidden");
        btnSearchUser.classList.add("visible");
        txtboxUserToSearch.classList.remove("hidden");
        txtboxUserToSearch.classList.add("visible");
        btnShowSearchUser.innerText = "Back To Users List";
    }
    else
    {
        usersListTable.classList.remove("hidden");
        usersListTable.classList.add("visible");
        searchedUserTable.classList.remove("visible");
        searchedUserTable.classList.add("hidden");
        btnSearchUser.classList.remove("visible");
        btnSearchUser.classList.add("hidden");
        txtboxUserToSearch.classList.remove("visible");
        txtboxUserToSearch.classList.add("hidden");
        btnShowSearchUser.innerText = "Search New Friends";
        ajaxGetUserList();
        emptyTable(searchedUserTable);
    }
    showUsersList = !showUsersList;
}

function ajaxGetUserList()
{
    let userListRequest = {
        username: username,
        parameter : ""
    };
    $.ajax({
        url: "http://localhost:8080/getUsers",
        type: "POST",
        data: JSON.stringify(userListRequest),
        dataType: "json",
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let userDTOList = serverResponse.responseMessage;
            if(userDTOList)
            {
                createUserListInHtml(userDTOList);
            }
            else
                alert("User not found");
        },
        error: function (xhr)
        {
            alert("Unauthorized Here Request!");
            location.href = "../";
        }
    });
}


function createUserListInHtml(userDTOList)
{
    let tableUsersList = document.getElementById("usersListTable");
    emptyTable(tableUsersList);
    while(user = userDTOList.pop())
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

        let tdStatus = document.createElement("td");
        tdStatus.className = "";
        let imgUserState = document.createElement("img");
        imgUserState.className = "imgUserState";
        imgUserState.src = (user.logged) ? "../img/online.png" : "../img/offline.png";
        imgUserState.alt ="img user state (online or offline)";
        tdStatus.append(imgUserState);
        trUser.append(tdStatus);

        let tdAction = document.createElement("td");
        tdAction.className = "";
        let btnRemoveUser = document.createElement("button");
        btnRemoveUser.className = "";
        btnRemoveUser.id = "btnRemoveUser&" + trUser.id;
        btnRemoveUser.innerText = "Remove User";
        btnRemoveUser.onclick = function (e) { onClickListenerBtnDeleteUser(this); };
        tdAction.append(btnRemoveUser);
        trUser.append(tdAction);

        tableUsersList.append(trUser);
    }
}

function emptyTable(table)
{
    while(table.childElementCount > 0)   // Delete all the old elemnt (if there are)
        table.removeChild(table.firstChild);
}

function onClickListenerBtnDeleteUser(button)
{
    const usernameToRemove = button.id.toString().split('&')[1];
    if (confirm("Sicuro di voler rimuovere " + usernameToRemove + " dalla lista utenti?")) {

        let removeUserRequest = {
            username: username,
            password : "admin",
            parameter : usernameToRemove
        };

        $.ajax({
            url: "http://localhost:8080/deleteUser",
            type: "POST",
            data:  JSON.stringify(removeUserRequest),
            dataType: "json",
            contentType: 'application/json',
            success: function (serverResponse)
            {
                let removeOperation = serverResponse.responseMessage;
                switch (removeOperation)
                {
                    case 0:
                        alert("The User " + usernameToRemove + " has been successfully removed.");
                        ajaxGetUserList();
                        break;
                    case 1:
                        alert("We're Sorry, an Error occurred during remove operation." +
                            " The User " + usernameToRemove + " has NOT been removed from your Users list");
                        break;
                    default:
                        break;
                }
            },
            error: function (xhr)
            {
                let responseMessage = xhr.responseText;
                alert("Error: " + responseMessage);
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
        username: username,
        password: "admin",
        parameter : usernameToSearch
    };

    $.ajax({
        url: "http://localhost:8080/getUsers",
        type: "POST",
        data: JSON.stringify(userSearchRequestDTO),
        dataType: "json",
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let searchedUserList = serverResponse.responseMessage;
            if (searchedUserList)
            {
                createTableSearchedUserInHtml(searchedUserList);
            }
            else {
                alert("- NOT FOUND - : The user [" + usernameToSearch + "] was not found into db");
            }
        },
        error: function (serverResponse)
        {
            alert("Unauthorized Request! You must be logged to navigate this page");
            location.href = "../";
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
        let btnRemoveUser = document.createElement("button");
        btnRemoveUser.className = "";
        btnRemoveUser.id = "btnRemoveUser&" + trUser.id;
        btnRemoveUser.innerText = "Remove User";
        btnRemoveUser.onclick = function (e) { onClickListenerBtnDeleteUser(this); };
        tdAction.append(btnRemoveUser);
        trUser.append(tdAction);

        searchedUserTable.append(trUser);
    }
}