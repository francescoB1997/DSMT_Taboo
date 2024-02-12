document.addEventListener("DOMContentLoaded", function(){
    checkLogIn();

    document.getElementById('logout').onclick = function () {
        if(confirm("Are u sure you want to log out?")){
            sessionStorage.removeItem("userLog");
            location.href = "../login.html";
        }
    }
})

function checkLogIn(){
    if(sessionStorage.getItem("userLog")!=="admin"){
        alert("You Must be logged as Admin to access this web page!")
        location.href = "../login.html";
    }
}