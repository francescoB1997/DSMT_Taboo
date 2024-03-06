const username = sessionStorage.getItem("userLog");
$(document).ready(function ()
{
    if(!checkLogin())
    {
        location.href = "../";
        return;
    }

    document.getElementById("btnCheckInvite").onclick = function (e) { onClickListenerBtnCheckInvite(); };
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

function onClickListenerBtnCheckInvite()
{
    $.ajax({
        url: "http://localhost:8080/checkInvite",
        type: "POST",
        data: username,
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let invite = serverResponse.responseMessage;
            if(invite === undefined) {
                alert("Nessun invito");
                return;
            }

            if(invite.rivals.pop() === username)
                alert("io sono uno dei rivali di " + invite.userInviter);
            else
                alert("io sono in squadra con " + invite.userInviter)
        },
        error: function ()
        {
            alert("Unauthorized Request!");
            location.href = "../";
        }
    });

}

function getRandomInt(min , max)
{
    let randomInt = 0;
    while(randomInt === 0) {
        min = Math.ceil(min);
        max = Math.floor(max);
        randomInt = Math.floor(Math.random() * (max - min + 1)) + min;
    }
    return randomInt;
}