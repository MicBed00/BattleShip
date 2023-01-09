var boardsList;

function adderShip(event) {
    var length = document.getElementById("len").value;
    var position = document.getElementById("orientation").value;
    var xstart;
    var target = event.target, col, row;
    var id;

    col = target.parentElement;
    row = col.parentElement;
    ystart = row.rowIndex;
    xstart = col.cellIndex;

    new BattleShipClient().addShip(length, xstart, ystart, position, (status, responseBody) => {

        //ten warunek chyba jest niepotrzebny bo numer status sprawdzam w call
        if (status >= 200 && status <= 299) {
            resetParam()
            id = responseBody;

            new BattleShipClient().getStatusGameFromDataBase(id, (status, responseBody) => {
                if (status >= 200 && status <= 299) {

                    boardsList = responseBody;
                    configurationGame(boardsList)
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


function configurationGame(boardList) {
    //wymyślić inny warunek dla odblokowania przycisku
    if (checkIfStillBoardPlayerOne(boardsList)) {
        renderShip(boardList[0])

        if (boardList[0].ships.length === shipNumber) {
            document.getElementById("renderTable").style.pointerEvents = "none";
            document.getElementById("accept").disabled = false;

            document.getElementById("accept").addEventListener("click", function () {
                renderShip(null);
                document.getElementById("renderTable").style.pointerEvents = "auto";
                document.getElementsByClassName("button").disabled = false;
                document.getElementById("accept").disabled = true;
                document.getElementById("ownerBoard").innerText = "Board's player 2";
            }, false);
        }
    } else if (checkIfBoardPlayerTwoIsFull(boardsList)) {
        renderShip(boardList[1])

        if (boardList[1].ships.length === shipNumber) {
            document.getElementById("renderTable").style.pointerEvents = "none";
            document.getElementById("accept").disabled = false;

            document.getElementById("accept").addEventListener("click", function () {
                document.getElementById("accept").disabled = true;
                location.replace("http://localhost:8080/view/added_Ship");
            }, false);
        }
    }
}


function checkIfBoardPlayerTwoIsFull(boardsList) {
    return boardsList[0].ships.length === shipNumber && boardsList[1].ships.length <= shipNumber

}

function checkIfStillBoardPlayerOne(boardslist) {
    return boardslist[0].ships.length <= shipNumber && boardslist[1].ships.length === 0;
    // return boardslist.curretnPlayer === 1;
}

function resumeGame() {
    //jeśli gracz potwierdzi, że chce zacząć grę od ostatniego zapisanego stanu gry wtedy odpytuję serwer o ostatni
    //zapisany rekord id z bazy i na jego podstawie odtwarzam stan gry
    document.getElementById("id_resumeGame").hidden = true;
    var idShip;
    new BattleShipClient().getShipId((status, responseBody) => {
        if (status >= 200 && status <= 299) {
            idShip = responseBody;

            //gdy mam już id to wysyłam request do pobrania rekordu z bazy
            new BattleShipClient().getStatusGameFromDataBase(idShip, (status, responseBody) => {
                if (status >= 200 && status <= 299) {
                    //renderowanie na tablicy wcześniej dodanych statków
                    boardsList = responseBody;
                    configurationGame(boardsList)
                    if(responseBody[0].ships.length > 0)
                        document.getElementById("backAction").disabled = false;

                    //odtworzenie stanu Listy Boardów po stronie serwera za pomocą metody POST
                    new BattleShipClient().restoringStateBoardListOnServer(idShip, (status, responseBody) => {
                        // if (status >= 200 && status <= 299)
                            //chyba nie potrzebuje zwrotki
                            alert("Zapis stanu boardów na serwerze!!")
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
    var table = renderShip(null); //jeśli ostatnia rozgrywka została zakończona to zaczynamy z czystą planszą
    return table;
}

document.getElementById("backAction").addEventListener("click", function () {
    var idShip;

    new BattleShipClient().getShipId((status, responseBody) => {
        if (status >= 200 && status <= 299) {
            idShip = responseBody;

            if (checkIfStillBoardPlayerOne(boardsList)) {
                new BattleShipClient().deleteLastAddedShip(0, idShip, (status, responseBody) => {

                    if (status >= 200 && status <= 299) {
                        document.getElementById("renderTable").style.pointerEvents = "auto";
                        document.getElementById("accept").disabled = true;
                        renderShip(responseBody[0]);
                    }
                }, (status, responseBody) => {
                    alert("Błąd " + status);
                })

            } else {
                new BattleShipClient().deleteLastAddedShip(1, idShip, (status, responseBody) => {
                    if (status >= 200 && status <= 299) {
                        document.getElementById("renderTable").style.pointerEvents = "auto";
                        document.getElementById("accept").disabled = true;
                        renderShip(responseBody[1]);
                    }
                }, (status, responseBody) => {
                    alert("Błąd " + status);
                })
            }
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
    var table;

    new BattleShipClient().getSetupsBoard((status, responseBody) => {
        shipNumber = responseBody;
    }, (status, responseBody) => {
        alert("Błąd przy pobieraniu ustawień " + responseBody)
    });


    new BattleShipClient().getShipId((status, responseBody) => {

        if (status >= 200 && status <= 299) {
            idShip = responseBody;

            //sprawdzam czy gra została zakończona
            new BattleShipClient().getPhaseGame(idShip,(status, responseBody) => {
                if (status >= 200 && status <= 299) {

                    var gameOver = responseBody //tu wyciągam wartość pola 'state' ze statusu rozgrywki

                    if (gameOver === 'FINISHED') {
                        table = renderShip(null); //jeśli ostatnia rozgrywka została zakończona to zaczynamy z czystą planszą
                        return table;
                    } else {
                        document.getElementById("id_resumeGame").hidden = false;
                    }
                }
            }, (status, responseBody) => {
                alert("Błąd przy sprawdzaniu statusu gry " + responseBody);
            })
        }

    }, (status, responseBody) => {
        alert("Błąd przy wznawianiu gry " + responseBody);
    });


}