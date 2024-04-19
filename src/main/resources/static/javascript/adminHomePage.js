const username = sessionStorage.getItem("userLog");
const IP_Server = "localhost:8084";

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
    let divWelcome = document.getElementById("h1WelcomeDiv_2");
    divWelcome.innerHTML = "Administrator";
}

function onClickListenerBtnLogout()
{
    let username = sessionStorage.getItem("userLog");
    $.ajax({
        url: "http://" + IP_Server + "/logout",
        type: "POST",
        data: username,
        dataType: "text",
        contentType: 'application/text',
        success: function ()
        {
            sessionStorage.removeItem("userLog");
            location.href = "./";
        },
        error: function (xhr)
        {
            let serverResponse = JSON.parse(xhr.responseText);
            alert(serverResponse.responseMessage);
            location.href = "./";
        }
    });
}

function onClickListenerBtnBrowesGames()
{
    location.href = "./adminMatchesListPage.html";
}

function onClickListenerBtnViewUsers()
{
    location.href = "./adminUserListPage.html";
}

