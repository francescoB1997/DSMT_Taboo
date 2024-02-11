$(document).ready(function () {
    document.getElementById('logoutBtn').onclick = function (e) { onClickListenerBtnLogout(); }
});

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
            alert("Errore: " + xhr.responseText);
        }
    });
}