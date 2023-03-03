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


checkIfIsFinished();
// document.getElementById("id_resumeGame").hidden = false;
resumeGame();

function resumeGame() {
    document.getElementById("id_resumeGame").hidden = true;
    configuration();
    disabledOwnerBoard();
    intervalId = setInterval(listenerShots, 500);
}

function disabledOwnerBoard() {
    new BattleShipClient().getUsers(gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            var userId = responseBody[0].id;
            if (userId == currentPlayerId) {
                board1.style.pointerEvents = "none";

            } else {
                board2.style.pointerEvents = "none";
            }
        }
    }, (status, responseBody) => {
        alert("Błąd "+ responseBody);

    })
}

function boardAccessControl(boardList) {
    var numberOppShot1 = boardList[0].opponentShots.length;
    var numberOppShot2 = boardList[1].opponentShots.length;

    if(numberOppShot1 === numberOppShot2) {
        board1.style.pointerEvents = "none";
        board2.style.pointerEvents = "auto";
    } else {
        board1.style.pointerEvents = "auto";
        board2.style.pointerEvents = "none";
    }
    disabledOwnerBoard();
}

function configuration() {
    new BattleShipClient().getStatusGameFromDataBase(gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            boardAccessControl(responseBody);
            renderShot(responseBody[0]);
            renderShot(responseBody[1]);
        }
    })
}

function listenerShots() {
    new BattleShipClient().getStatusGameFromDataBase(gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            var boardList = responseBody;
            boardAccessControl(boardList);
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


