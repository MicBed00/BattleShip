//zmienne wykorzystuję w deklaracji id komórek tabeli. Są konieczne przy renderowaniu strzałów w celu odróżnienia id komórek między tabelami
var ply1, ply2;
ply1 = "Ply1";
ply2 = "Ply2";
var board1;
var board2;
renderBoards();
var currentBoard = board1;
var userId = document.getElementById("user_id").value;
checkIfIsFinished();
document.getElementById("id_resumeGame").hidden = false;
//TODO błąd - po przejściu do okna strzelania zawsze pojawia się okno Resume Game, nawet w sytuacji gdy nikt nie przerywa gry
function resumeGame() {
    //TODO przy wznawianiu gry muszę sprawdzić na jakiej tablicy zakończyło się strzelanie i od jakiej tab zacząć strzelać
    //aktualnie po wznowieniu tablica ładuje się ZAWSZE z domyślnym currentBoard = board1

    document.getElementById("id_resumeGame").hidden = true;

            new BattleShipClient().getStatusGameFromDataBase(userId, (status, responseBody) => {
                if (status >= 200 && status <= 299) {
                    if(responseBody[0].opponentShots.length < responseBody[1].opponentShots.length) {
                        currentBoard = board2;
                        renderShot(responseBody);
                        currentBoard = board1;
                        renderShot(responseBody);
                        currentBoard = board2;
                    } else {
                        renderShot(responseBody);
                        currentBoard = board2;
                        renderShot(responseBody);
                        currentBoard = board1;
                    }
                    //odtworzenie stanu Boardów w programie na serwerze za pomocą metody POST
                    new BattleShipClient().restoringStateBoardListOnServer(userId, (status, responseBody) => {
                        // if (status >= 200 && status <= 299)
                        //chyba nie potrzebuje zwrotki

                    }, (status, responseBody) => {
                        alert("Błąd przy odtwarzaniu stanu gry " + responseBody);
                    });
                }
            })

}
function startNewGame() {
    document.getElementById("id_resumeGame").hidden = true;
    renderShot(null); //jeśli ostatnia rozgrywka została zakończona to zaczynamy z czystą planszą
}

function shotAtShip(event) {
    var target = event.target, col, row, shotX, shotY;
    col = target.parentElement;
    row = col.parentElement;
    shotY = row.rowIndex;
    shotX = col.cellIndex;
    const tbody = row.parentElement;
    const table = tbody.parentElement;

    if(table !== currentBoard) {
        new BattleShipClient().shooterShip(shotX, shotY, gameId,(status, responseBody) => {
            if(status >= 200 && status <= 299) {

                renderShot(responseBody, target);
                currentBoard = currentBoard === board1 ? board2 : board1;
            }
        }, (status, responseBody) => {
            if(status === 400) {
                alert("Błąd renderowania strzału" + responseBody);
            }
        });
    } else {
        alert("Invalid board")
    }
}


function checkIfIsFinished() {
    new BattleShipClient().getterStatusGame(userId,(status, responseBody) => {
        if(responseBody === true) {

            alert("Koniec gry");
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
        //TODO do przypomnienia sobie ta składnia z if
        for(const shot of responseBody[index].opponentShots) {
            //TODO zmienić w warunku if "HIT" na daną zaciąganą z serwera
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


