const username = sessionStorage.getItem("userLog");

$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "./";
        return;
    }
    setWelcomeText();
    document.getElementById('logoutBtn').onclick = function (e) { onClickListenerBtnLogout(); };
    document.getElementById('startMathBtn').onclick = function (e) { onClickListenerBtnStartMatch(); };
    document.getElementById('viewFriendsBtn').onclick = function (e) { onClickListenerBtnViewFriends(); };
    document.getElementById("viewMatchesBtn").onclick = function (e) { onClickListenerBtnViewMatches(); };
});

function checkLogin()
{
    if(!username)
    {
        alert("You're not logged");
        return false;
    }
    return true;
}

function setWelcomeText()
{
    let divWelcome = document.getElementById("h1WelcomeDiv_2");
    divWelcome.innerHTML = username;
}

function onClickListenerBtnLogout()
{
    let username = sessionStorage.getItem("userLog");
    $.ajax({
        url: "./logout",
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

function onClickListenerBtnStartMatch()
{
    //Contattare servlet per la creazione del team, oppure reindirizzare verso una createTeams.html
    //in cui selezionare da una lista di AMICI ONLINE quelli da inserire nella squadra (se è semplice, sarebbbe carino il drag and drop)
    location.href = "./startMatchPage.html";
}

function onClickListenerBtnViewFriends()
{
    //Fare una chiamata asincrona AJAX per ottenere la lista degli amici, ossia un JSON con una lista di nomi
    // e per ognuno, ci vede essere l'info se è Online o meno.
    // Per ogni amico, va creato a runTime un elemento HTML per mostrarlo.

    location.href = "./friendListPage.html";
}

function onClickListenerBtnViewMatches()
{
    location.href = "./matchesListPage.html";
}
