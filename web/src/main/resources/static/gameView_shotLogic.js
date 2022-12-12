//zmienne wykorzystuję w deklaracji id komórek tabeli. Są konieczne przy renderowaniu strzałów w celu odróżnienia id komórek między tabelami
var ply1, ply2;
ply1 = "Ply1";
ply2 = "Ply2";
var board1;
var board2;
renderBoards();
var currentBoard = board1;

function shotAtShip(event) {
    var target = event.target, col, row, shotX, shotY;
    col = target.parentElement;
    row = col.parentElement;
    shotY = row.rowIndex;
    shotX = col.cellIndex;
    const tbody = row.parentElement;
    const table = tbody.parentElement;

    if(table !== currentBoard) {
        new BattleShipClient().shooterShip(shotX, shotY, (status, responseBody) => {
            console.log("zwrotka z serwera " + status);
            if(status >= 200 && status <= 299) {
                renderShot(responseBody, target);
                currentBoard = currentBoard === board1 ? board2 : board1;
            }
        }, (status, responseBody) => {
            if(status === 400) {
                alert("SDDSDs");
            }
            // throw responseBody
            // alert("Błąd " + responseBody)
        });
    } else {
        alert("Invalid board")
    }
}


function checkIfIsFinished() {
    new BattleShipClient().getterStatusGame((status, responseBody) => {
        if(responseBody === true) {
            alert("Koooniec");
            window.location.href = "/view/statistics";
        }
    }, (status, responseBody) => {
        alert("Błąd "+ responseBody)
    })
}
function renderBoards() {
    board1 = createBoard(ply1);
    board2 = createBoard(ply2);
    document.getElementById("boardPlayer1").appendChild(board1);
    document.getElementById("boardPlayer2").appendChild(board2);
}

function renderShot(responseBody) {
    if(responseBody != null) {
        var boardShot = currentBoard === board1 ? board2 : board1;
        var index = currentBoard === board1 ? 1 : 0;
        for(const shot of responseBody[index].opponentShots) {
            // w tym warunku chciałbym wykorzystać składnię -> shot.state.equals(Shot.State.HIT)
            if(shot.state === "HIT") {
                boardShot.getElementsByClassName(shot.x + "_" +shot.y).item(0).style.backgroundColor = "red";
            } else if(shot.state === "MISSED") {
                var ox = boardShot.getElementsByClassName(shot.x + "_" +shot.y).item(0);// boardShot.getElementsByClassName(shot.x + "_" +shot.y) - zwraca kolekcję htmlcollection
                ox.style.backgroundColor = "black";
            }
        }
    }
    checkIfIsFinished();
}
