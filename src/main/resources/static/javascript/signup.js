
$(document).ready(function ()
{
    document.getElementById('btnSignUp').onclick = function (e) { onClickListenerBtnSignup(); }
});

function onClickListenerBtnSignup()
{
    let name = document.getElementById('txtboxName').value;
    let username = document.getElementById('txtboxUsername').value;
    if(username === "" || name === "")
        return;

    let password = document.getElementById('txtboxPassword').value;
    let repassword = document.getElementById('txtboxRePassword').value;
    
    if (password !== repassword) {
        alert("Passwords do not match. Please re-enter.");
        return;
    }

    let passwordRegex = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])\S{8,}$/;

    if (!passwordRegex.test(password)) {
        alert("Password must be at least 8 characters long and include at least one number, one alphabet, and one symbol.");
        return;
    }

    alert("Spring post login");
}