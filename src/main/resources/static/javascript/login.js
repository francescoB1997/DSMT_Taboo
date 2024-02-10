
$(document).ready(function () {
    document.getElementById('login_btn').onclick = function (e) { onClickListenerBtnLogin(); }
});

function onClickListenerBtnLogin()
{
    let username = document.getElementById('txtboxUsername').value
    let password = document.getElementById('txtboxPassword').value

    if(username === "admin" && password === "admin")
    {
        location.href = "./admin.html"
        sessionStorage.setItem("userLog","admin");
    }

    let loginRequest = {
        username : username,
        password : password
    };

    alert("Da implementare il Servlet");
    /*
    $.ajax({
        url : "http://127.0.0.1:8080/login",
        data : JSON.stringify(loginRequest),
        type : "POST",
        dataType: "json",
        contentType: 'application/json',
        success: function () {
            sessionStorage.setItem("userLog",username);
            sessionStorage.setItem("gameId","");
            location.href = "./playerMainPage.html"
        },
        error: function(xhr) {
            let response = JSON.parse(xhr.responseText)
            alert("Non puoi loggarti -> " + response.answer)
        }
    })
     */
}