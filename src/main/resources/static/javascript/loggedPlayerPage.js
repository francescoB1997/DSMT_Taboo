
const username = sessionStorage.getItem("userLog");

$(document).ready(function () {
    checkLogin();
    setWelcomeText();
    document.getElementById('logoutBtn').onclick = function (e) { onClickListenerBtnLogout(); }
});

function checkLogin()
{
    //alert("checkLogin: " + username);
    if(!username)
    {
        //alert("You're not login");
        location.href = "../login.html";
    }
}

function setWelcomeText()
{
    let divWelcome = document.getElementById("loginText");
    divWelcome.innerHTML = "You're logged as: " + username;
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
            location.href = "../login.html";
        },
        error: function (xhr)
        {
            let serverResponse = JSON.parse(xhr.responseText);
            alert(serverResponse.responseMessage)
        }
    });
}