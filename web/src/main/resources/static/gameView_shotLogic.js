//zmienne wykorzystuję w deklaracji id komórek tabeli. Są konieczne przy renderowaniu strzałów w celu odróżnienia id komórek między tabelami
var ply1, ply2;
ply1 = "Ply1";
ply2 = "Ply2";
var board1;
var board2;
renderBoards();
var currentBoard = board1;
var currentPlayerId = document.getElementById("user_id").value;
var gameId = document.getElementById("game_id").value;
var intervalId;
var indexUser;

checkIfIsFinished();
// document.getElementById("id_resumeGame").hidden = false;
resumeGame();

function resumeGame() {
    document.getElementById("id_resumeGame").hidden = true;
    configuration();
    // disabledBoard();
    intervalId = setInterval(listenerShots, 1000);

}

function disabledBoard() {
    new BattleShipClient().getUsers(gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            var userId = responseBody[0].id;
            if (userId == currentPlayerId) {
                disableButtonListeners("tableBoard" + ply1);
                indexUser = 0;
            } else {
                disableButtonListeners("tableBoard" + ply2);
                indexUser = 2;
            }
        }
    }, (status, responseBody) => {

    })
}

function disableButtonListeners(tableId) {
    const table = document.getElementById(tableId);
    const buttons = table.getElementsByTagName("button");
    for (let i = 0; i < buttons.length; i++) {
        const button = buttons[i];
        button.removeEventListener("click", shotAtShip, true);
    }
}

function configuration() {
    new BattleShipClient().getStatusGameFromDataBase(gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            renderShot(responseBody[0]);
            renderShot(responseBody[1]);
        }
    })
}
//TODO przygotować funkcję, która będzie porównywała rozmiar zbioru opponentShots i na tej podstawie
// blokowała diva w którym jest tablica przeciwnka. Dzięki temu będzie można zachować turowość gry i będzie
//można oddać strzał bez uprzedzania ruchu przeciwnika. Zakładam, że zaczyna zawsze gracz, który tworzył game
// więc w List będzie zapisany jako pierwszy
// Funkcja ta również będzie musiała nasłuchiwać, dlatego może będę mógł wykorzystać tego lisnera bo w pierwszym requesie dostaje
// listę Boardów i z nich mogę wyciągnąć opponents shot
function listenerShots() {
    new BattleShipClient().getStatusGameFromDataBase(gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            var boardList = responseBody;
                new BattleShipClient().getUsers(gameId, (status, responseBody) => {
                    if (status >= 200 && status <= 299) {
                        var userId = responseBody[0].id;
                            if (userId == currentPlayerId) {
                                currentBoard = board1;
                                renderShot(boardList[0]);
                            } else {
                                currentBoard = board2;
                                renderShot(boardList[1]);
                            }
                    }
                }, (status, responseBody) => {
                    alert("Błąd "+ responseBody);
                })
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

    new BattleShipClient().shooterShip(shotX, shotY, gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            var boardList = responseBody;
            new BattleShipClient().getUsers(gameId, (status, responseBody) => {
                if (status >= 200 && status <= 299) {
                    var userId = responseBody[0].id;

                    if (userId == currentPlayerId) {
                        currentBoard = board2;
                        renderShot(boardList[1], target);
                    } else {
                        currentBoard = board1;
                        renderShot(boardList[0], target);
                    }
                }
            }, (status, responseBody) => {

            })
        }
    }, (status, responseBody) => {
        if (status === 400) {
            alert("Błąd renderowania strzału" + responseBody);
        }
    });
}


function checkIfIsFinished() {
    new BattleShipClient().getterStatusGame(currentPlayerId, gameId, (status, responseBody) => {
        if (responseBody === true) {
            alert("Koniec gry");
            window.location.href = "/view/statistics";
        }
    }, (status, responseBody) => {
        alert("Błąd " + responseBody)
    })
}

function renderBoards() {
    board1 = createBoard(ply1);
    board2 = createBoard(ply2);
    document.getElementById("boardPlayer1").appendChild(board1);
    document.getElementById("boardPlayer2").appendChild(board2);
}

// function renderShot(responseBody) {
//     if(responseBody != null) {
//         var boardShot = currentBoard === board1 ? board2 : board1;
//         var index = currentBoard === board1 ? 1 : 0;
//         //TODO do przypomnienia sobie ta składnia z if
//         for(const shot of responseBody[index].opponentShots) {
//             //TODO zmienić w warunku if "HIT" na daną zaciąganą z serwera
//             if(shot.state === "HIT") {
//                 boardShot.getElementsByClassName(shot.x + "_" +shot.y).item(0).style.backgroundColor = "red";
//             } else if(shot.state === "MISSED") {
//                 var ox = boardShot.getElementsByClassName(shot.x + "_" +shot.y).item(0);// boardShot.getElementsByClassName(shot.x + "_" +shot.y) - zwraca kolekcję htmlcollection
//                 ox.style.backgroundColor = "black";
//             }
//         }
//     }
//     checkIfIsFinished();
// }

function renderShot(responseBody) {
    if (responseBody != null) {
        var boardShot = currentBoard;
        for (const shot of responseBody.opponentShots) {
            //TODO zmienić w warunku if "HIT" na daną zaciąganą z serwera
            if (shot.state === "HIT") {
                boardShot.getElementsByClassName(shot.x + "_" + shot.y).item(0).style.backgroundColor = "red";
            } else if (shot.state === "MISSED") {
                var ox = boardShot.getElementsByClassName(shot.x + "_" + shot.y).item(0);// boardShot.getElementsByClassName(shot.x + "_" +shot.y) - zwraca kolekcję htmlcollection
                ox.style.backgroundColor = "black";
            }
        }
    }
    checkIfIsFinished();
}


