const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    checkLogin();
    ajaxGetFriendList();

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
        btnRemoveFriend.innerText = "Remove Friend";
        btnRemoveFriend.onclick = function (e) { onClickListenerBtnRemoveFriends(); };
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

function onClickListenerBtnRemoveFriends()
{
    alert("Rimuovi amico");
}