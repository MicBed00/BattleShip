var board;
var userId = document.getElementById("user_id").value;
var gameId = document.getElementById("game_id").value;

function adderShip(event) {
    var length = document.getElementById("len").value;
    var position = document.getElementById("orientation").value;
    var xstart;
    var target = event.target, col, row;

    col = target.parentElement;
    row = col.parentElement;
    ystart = row.rowIndex;
    xstart = col.cellIndex;

    new BattleShipClient().addShip(length, xstart, ystart, position, userId, gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            resetParam()

            new BattleShipClient().getBoard(gameId, userId,(status, responseBody) => {
                if (status >= 200 && status <= 299) {

                    board = responseBody;
                    configurationGame(board)
                }

            }, (status, responseBody) => {
                alert("Błąd przy pobieraniu list statków " + responseBody);
            })
        }

    }, (status, responseBody) => {
        if (status >= 400 && status <= 499) {
            alert(responseBody);
            //Uwaga: sprawdź czy po wyrzuceniu błędu statek mimo to nie jest dodawny do listy!
        }
    });
}

function resetParam() {
    //reset parametrów do domyślnych
    document.getElementById("len").value = 1;
    document.getElementById("orientation").value = "VERTICAL";
    document.getElementById("backAction").disabled = false;
}

function configurationGame(board) {
    //wymyślić inny warunek dla odblokowania przycisku
    renderShip(board)

    if (board.ships.length === shipNumber) {
        document.getElementById("renderTable").style.pointerEvents = "none";
        document.getElementById("accept").disabled = false;

        document.getElementById("accept").addEventListener("click", function () {
            document.getElementById("accept").disabled = true;
                    window.location.href = "/view/added_Ship/" + gameId;
        }, false);
    }

    // if (checkIfStillBoardPlayerOne(board)) {
    //     renderShip(boardList)
    //
    //     if (boardList[0].ships.length === shipNumber) {
    //         document.getElementById("renderTable").style.pointerEvents = "none";
    //         document.getElementById("accept").disabled = false;
    //
    //         document.getElementById("accept").addEventListener("click", function () {
    //             renderShip(null);
    //             document.getElementById("renderTable").style.pointerEvents = "auto";
    //             document.getElementsByClassName("button").disabled = false;
    //             document.getElementById("accept").disabled = true;
    //             document.getElementById("ownerBoard").innerText = "Board's player 2";
    //         }, false);
    //     }
    // } else if (checkIfBoardPlayerTwoIsNotFull(board)) {
    //     document.getElementById("ownerBoard").innerText = "Board's player 2";
    //     renderShip(boardList[1])
    //
    //     if (boardList[1].ships.length === shipNumber) {
    //         document.getElementById("renderTable").style.pointerEvents = "none";
    //         document.getElementById("accept").disabled = false;
    //
    //         document.getElementById("accept").addEventListener("click", function () {
    //             document.getElementById("accept").disabled = true;
    //             location.replace("http://localhost:8080/view/added_Ship");
    //         }, false);
    //     }
    // }
}


function checkIfBoardPlayerTwoIsNotFull(boardsList) {
    return boardsList[0].ships.length === shipNumber && boardsList[1].ships.length <= shipNumber

}

function checkIfStillBoardPlayerOne(boardslist) {
    return boardslist[0].ships.length <= shipNumber && boardslist[1].ships.length === 0;
}

function resumeGame() {
            // new BattleShipClient().getStatusGameFromDataBase(gameId, (status, responseBody) => {
            //     if (status >= 200 && status <= 299) {
            //         //renderowanie na tablicy wcześniej dodanych statków
            //         board = responseBody;
            //         configurationGame(board)
            //         if(responseBody[0].ships.length > 0)
            //             document.getElementById("backAction").disabled = false;
            //     }
            // })

    new BattleShipClient().getBoard(gameId, userId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            //renderowanie na tablicy wcześniej dodanych statków
            board = responseBody;
            configurationGame(board)
            if(responseBody.ships.length > 0)
                document.getElementById("backAction").disabled = false;
        }
    })
}

function startNewGame() {
    // document.getElementById("id_resumeGame").hidden = true;
    //TODO do sprawdzenia ta część
    new BattleShipClient().updateStatusGame(userId,"REJECTED",(status, responseBody) => {
            if (status >= 200 && status <= 299) {
                //Zapisuję nową grę do tabeli games
                new BattleShipClient().saverNewGame(userId,(status, responseBody) => {
                    if (status >= 200 && status <= 299) {
                        table = renderShip(null);
                        return table;
                    }

                }, (status, responseBody) => {
                    alert("Błąd przy przerwaniu gry " + responseBody)
                })
                    }
                }, (status, responseBody) => {
                    alert("Błąd przy zapisywaniu nowej gry " + responseBody);
                })

}

document.getElementById("backAction").addEventListener("click", function () {
    new BattleShipClient().getStatusGameFromDataBase(gameId,(status, responseBody) => {
        if (status >= 200 && status <= 299) {
            board = responseBody;
                new BattleShipClient().deleteLastAddedShip(userId, gameId,  (status, responseBody) => {
                    if (status >= 200 && status <= 299) {
                        document.getElementById("renderTable").style.pointerEvents = "auto";
                        document.getElementById("accept").disabled = true;
                        renderShip(responseBody);
                        if(responseBody.ships.length === 0) {
                            document.getElementById("backAction").disabled = true;
                        }
                    }
                }, (status, responseBody) => {
                    alert("Błąd " + status);
                })


        }

    }, (status, responseBody) => {
        alert("Błąd przy pobieraniu id Ship " + responseBody)
    });

}, false);


function renderShip(responseBody) {
    // var horizontalOrientation = [[${orientList}]][1];
    const table = document.createElement("table");
    table.id = "tableRes";
    table.className = "tableResponse";
    const tbBody = document.createElement("tbody");

    for (let i = 0; i < 10; i++) {

        const row = document.createElement("tr");
        row.className = "row";

        for (let j = 0; j < 10; j++) {

            const cell = document.createElement("td");
            cell.className = "cell";
            const button = document.createElement("button");
            button.className = "button";
            button.id = j + "&" + i
            button.addEventListener("click", function (event) {
                adderShip(event);
            }, false);
            cell.appendChild(button);

            if (responseBody != null) {
                for (let k = 0; k < responseBody.ships.length; k++) {
                    // if (responseBody.ships[k].position === horizontalOrientation)
                    if (responseBody.ships[k].position === "HORIZONTAL") {

                        if (responseBody.ships[k].ystart === i && responseBody.ships[k].xstart >= j
                            && (responseBody.ships[k].xstart - responseBody.ships[k].length + 1) <= j)
                            cell.style.backgroundColor = "yellow";
                    } else {

                        if (responseBody.ships[k].xstart === j && responseBody.ships[k].ystart <= i
                            && (responseBody.ships[k].ystart + responseBody.ships[k].length - 1) >= i)
                            cell.style.backgroundColor = "yellow";
                    }
                }
            }
            row.appendChild(cell);
        }
        tbBody.appendChild(row);
    }
    table.appendChild(tbBody);
    document.getElementById("renderTable").innerHTML = ""; // czyści diva
    document.getElementById("renderTable").appendChild(table);

    return table;
}


var shipNumber;
window.onload = setup();

function setup() {
    new BattleShipClient().getSetupsBoard((status, responseBody) => {
        shipNumber = responseBody;
    }, (status, responseBody) => {
        alert("Błąd przy pobieraniu ustawień " + responseBody)
    });
    resumeGame();
}