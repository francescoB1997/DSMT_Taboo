$(document).ready(function ()
{
    document.getElementById('btnSignUp').onclick = function (e) { onClickListenerBtnSignup(); }
});

function onClickListenerBtnSignup()
{
    let name = document.getElementById('txtboxName').value;
    let surname = document.getElementById('txtboxSurname').value;
    let username = document.getElementById('txtboxUsername').value;

    if( (username === "") || (name === "") || (surname === ""))
        return;

    let password = document.getElementById('txtboxPassword').value;

       let passwordRegex = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])\S{8,}$/;
       if (!passwordRegex.test(password)) {
           alert("Password must be at least 8 characters long and include at least one number, one alphabet, and one symbol.");
           return;
       }

    let rePassword = document.getElementById('txtboxRePassword').value;

    if (password !== rePassword) {
        alert("Passwords do not match. Please re-enter it and try again.");
        return;
    }

    let newUser = {
        username : username,
        name : name,
        surname : surname,
        password : password
    };

    $.ajax({
        url : "./signup",
        data : JSON.stringify(newUser),
        type : "POST",
        contentType: 'application/json',
        success: function ()
        {
            alert("You're Been Successfully Registered");
            location.href = "./";
        },
        error: function (xhr)
        {
            if(xhr.status === 400) // BAD REQUEST
                alert("The username is already used.")
            else if(xhr.status === 502) // BAD GATEWAY
                alert("Service Temporary Unavailable");
            else
            {
                alert("Unauthorized Request!");
                location.href = "./";
            }
        }
    });
}