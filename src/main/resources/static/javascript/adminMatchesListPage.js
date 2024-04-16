const username = sessionStorage.getItem("userLog");
const IP_Server = "10.2.1.130:5050";

$(document).ready(function ()
{
    if(!checkAdminLogin())
    {
        location.href = "../";
        return;
    }
    ajaxGetMatchesList();

});

function checkAdminLogin()
{
    if(!username || (username !== "admin"))
    {
        //alert("You're not logged");
        return false;
    }
    return true;
}

function ajaxGetMatchesList()
{
    let adminRequest = {
        username : username,
        parameter : ""
    };
    $.ajax({
        url: "http://" + IP_Server + "/getAllMatches",
        type: "POST",
        data: JSON.stringify(adminRequest),
        contentType: 'application/json',
        success: function (serverResponse)
        {
            let matchesList = serverResponse.responseMessage;
            if(matchesList)
                createMatchesListInHtml(matchesList);
            else
                alert("matchesList vuota");
        },
        error: function ()
        {
            alert("Unauthorized Request!");
            location.href = "../";
        }
    });
}

function createMatchesListInHtml(matchesList)
{
    let matchesListTableBody = document.getElementById("matchesListTable");
    emptyTable(matchesListTableBody);
    while(match = matchesList.pop())
    {
        let trMatch = document.createElement("tr");
        trMatch.id = "M_" + match.matchId;

        let tdMatchId = document.createElement("td");
        let pMatchId = document.createElement("p");
        pMatchId.innerText = match.matchId;
        tdMatchId.append(pMatchId);
        trMatch.append(tdMatchId);

        let tdTeam1 = document.createElement("td");
        let pTeam1Users = document.createElement("p");
        pTeam1Users.innerText = match.inviterTeam.toString().replace('[', '').replace(']', '');

        tdTeam1.append(pTeam1Users);
        trMatch.append(tdTeam1);

        let tdTeam2 = document.createElement("td");
        let pTeam2Users = document.createElement("p");
        pTeam2Users.innerText = match.rivalTeam.toString().replace('[', '').replace(']', '');
        tdTeam2.append(pTeam2Users);
        trMatch.append(tdTeam2);

        let tdScoreTeam1 = document.createElement("td");
        let pScoreTeam1 = document.createElement("p");
        pScoreTeam1.innerText = match.scoreInviterTeam;
        tdScoreTeam1.append(pScoreTeam1);
        trMatch.append(tdScoreTeam1);

        let tdScoreTeam2 = document.createElement("td");
        let pScoreTeam2 = document.createElement("p");
        pScoreTeam2.innerText = match.scoreRivalTeam;
        tdScoreTeam2.append(pScoreTeam2);
        trMatch.append(tdScoreTeam2);

        matchesListTableBody.append(trMatch);
    }
}

function emptyTable(table)
{
    while(table.childElementCount > 0)
        table.removeChild(table.firstChild);
}