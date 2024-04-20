
$(document).ready(function ()
{
    document.getElementById("loginBtn").onclick = function (e) { onClickListenerBtnLogin(); };
    document.getElementById("txtboxUsername").addEventListener("keypress", handlerEnterKeyPress);
    document.getElementById("txtboxPassword").addEventListener("keypress", handlerEnterKeyPress);
});

function handlerEnterKeyPress(event)
{
    if (event.key === "Enter")
    {
        event.preventDefault();
        onClickListenerBtnLogin();
    }
}

function onClickListenerBtnLogin()
{
    let username = document.getElementById('txtboxUsername').value
    let password = document.getElementById('txtboxPassword').value

    if(username === "" || password === "")
    {
        alert("Please, complete all fields");
        return;
    }

    let loginRequest = {
        username : username,
        password : password
    };

    $.ajax({
        url : "./login",
        data : JSON.stringify(loginRequest),
        type : "POST",
        dataType: "json",
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let responseMsg = serverResponse.responseMessage;
            if(responseMsg === "LoginAdminOK")
            {
                sessionStorage.setItem("userLog", username);
                location.href = "./adminHomePage.html"; // Rember that we're always in the Context Path
                return;
            }
            else
            {
                sessionStorage.setItem("userLog", username);
                location.href = "./loggedPlayerPage.html";
                return;
            }
        },
        error: function (xhr)
        {
            if(xhr.status === 400) // BAD REQUEST
                alert("Login Error. Please, check the Username or Password");
            else if(xhr.status === 502) // BAD GATEWAY
                alert("Service Temporary Unavailable");
            else
            {
                alert("Unauthorized Request!");
                location.href = "./";
            }
        }
    })
}