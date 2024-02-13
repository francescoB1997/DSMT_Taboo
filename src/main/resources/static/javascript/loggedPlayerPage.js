
const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    //checkLogin();
    setWelcomeText();
    document.getElementById('logoutBtn').onclick = function (e) { onClickListenerBtnLogout(); };
    document.getElementById('createTeamBtn').onclick = function (e) { onClickListenerBtnCreateTeam(); };
    document.getElementById('viewFriendsBtn').onclick = function (e) { onClickListenerBtnViewFriends(); };
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

function onClickListenerBtnCreateTeam()
{
    //Contattare servlet per la creazione del team, oppure reindirizzare verso una createTeams.html
    //in cui selezionare da una lista di AMICI ONLINE quelli da inserire nella squadra (se è semplice, sarebbbe carino il drag and drop)
}

function onClickListenerBtnViewFriends()
{
    //Fare una chiamata asincrona AJAX per ottenere la lista degli amici, ossia un JSON con una lista di nomi
    // e per ognuno, ci vede essere l'info se è Online o meno.
    // Per ogni amico, va creato a runTime un elemento HTML per mostrarlo.
}