//zmienne wykorzystuję w deklaracji id komórek tabeli. Są konieczne przy renderowaniu strzałów w celu odróżnienia id komórek między tabelami
var ply1, ply2;
ply1 = "Ply1";
ply2 = "Ply2";
var board1;
var board2;
renderBoards();
var currentBoard = board1;
checkIfIsFinished();
document.getElementById("id_resumeGame").hidden = false;

function resumeGame() {
    document.getElementById("id_resumeGame").hidden = true;
    var idShip;
    new BattleShipClient().getId((status, responseBody) => {
        if (status >= 200 && status <= 299) {
            idShip = responseBody;

            //gdy mam już id to wysyłam request do pobrania rekordu z bazy
            new BattleShipClient().getStatusGameFromDataBase(idShip, (status, responseBody) => {
                if (status >= 200 && status <= 299) {

                   renderShot(responseBody);
                   currentBoard = board2;
                   renderShot(responseBody);
                   currentBoard = board1;

                    //odtworzenie stanu Boardów w programie na serwerze za pomocą metody POST
                    new BattleShipClient().restoringStateBoardListOnServer(idShip, (status, responseBody) => {
                        // if (status >= 200 && status <= 299)
                        //chyba nie potrzebuje zwrotki

                    }, (status, responseBody) => {
                        alert("Błąd przy odtwarzaniu stanu gry " + responseBody);
                    });
                }
            })
        }

    }, (status, responseBody) => {
        alert("Błąd przy wznawianiu gry " + responseBody);
    });
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
        new BattleShipClient().shooterShip(shotX, shotY, (status, responseBody) => {
            if(status >= 200 && status <= 299) {
                // new BattleShipClient().getStatusGameFromDataBase()

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
    new BattleShipClient().getterStatusGame((status, responseBody) => {
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


