const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }
    setWelcomeText();
    document.getElementById('logoutBtn').onclick = function (e) { onClickListenerBtnLogout(); };
    document.getElementById('browesGamesBtn').onclick = function (e) { onClickListenerBtnBrowesGames(); };
    document.getElementById('viewUsersBtn').onclick = function (e) { onClickListenerBtnViewUsers(); };
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

function setWelcomeText()
{
    let divWelcome = document.getElementById("h1WelcomeDiv");
    divWelcome.innerHTML = "Welcome to Administrator Page";
}

function onClickListenerBtnLogout()
{
    let username = sessionStorage.getItem("userLog");
    $.ajax({
        url: "http://localhost:8080/logout",
        type: "POST",
        data: username,
        dataType: "text",
        contentType: 'application/text',
        success: function ()
        {
            sessionStorage.removeItem("userLog");
            location.href = "../";
        },
        error: function (xhr)
        {
            let serverResponse = JSON.parse(xhr.responseText);
            alert(serverResponse.responseMessage);
            location.href = "../";
        }
    });
}

function onClickListenerBtnBrowesGames()
{
    location.href = "../adminMatchesListPage.html";
}

function onClickListenerBtnViewUsers()
{
    location.href = "../adminUserListPage.html";
}

