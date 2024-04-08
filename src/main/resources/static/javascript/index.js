
$(document).ready(function ()
{
    document.getElementById("loginBtn").onclick = function (e) { onClickListenerBtnLogin(); };

    document.getElementById("txtboxUsername").addEventListener("keypress", handlerEnterKeyPress);
    document.getElementById("txtboxPassword").addEventListener("keypress", handlerEnterKeyPress);
});

function handlerEnterKeyPress(event) {
    if (event.key === "Enter") {
        event.preventDefault(); // Per evitare l'invio del modulo (se presente)
        onClickListenerBtnLogin();
    }
}

function onClickListenerBtnLogin()
{
    let username = document.getElementById('txtboxUsername').value
    let password = document.getElementById('txtboxPassword').value

    let loginRequest = {
        username : username,
        password : password
    };

    $.ajax({
        url : "http://localhost:8080/login",
        data : JSON.stringify(loginRequest),
        type : "POST",
        dataType: "json",
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let responseMsg = serverResponse.responseMessage;
            if(responseMsg === "LoginAdminOK"){
                sessionStorage.setItem("userLog", username);
                //sessionStorage.setItem("gameId","");
                location.href = "../adminHomePage.html";
                return;
            } else {
                sessionStorage.setItem("userLog", username);
                //sessionStorage.setItem("gameId","");
                location.href = "../loggedPlayerPage.html";
                return;
            }
        },
        error: function(xhr)
        {
            let serverResponse = JSON.parse(xhr.responseText);
            alert(serverResponse.responseMessage);
        }
    })

}