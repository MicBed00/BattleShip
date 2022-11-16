//zmienne wykorzystuję w deklaracji id komórek tabeli. Są konieczne przy renderowaniu strzałów w celu odróżnienia id komórek między tabelami
var ply1, ply2;
ply1 = "Ply1";
ply2 = "Ply2";
var board1;
var board2;
renderBoards();
var currentBoard = board1;

// onload = renderBoardPly2(ply2);


function shotAtShip(event) {
    var target = event.target, col, row, shotX, shotY;
    col = target.parentElement;
    row = col.parentElement;
    shotY = row.rowIndex;
    shotX = col.cellIndex;
    const tbody = row.parentElement;
    const table = tbody.parentElement;
    console.log("Id tbody albo table  " + table.getAttribute("id"));

    if(table !== currentBoard) {
        new BattleShipClient().shooterShip(shotX, shotY, (status, responseBody) => {
            console.log("zwrotka z serwera " + status);
            if(status >= 200 && status <= 299) {
                renderShot(responseBody, target);
                currentBoard = currentBoard === board1 ? board2 : board1;
            }
        }, (status, responseBody) => {
            alert("Błąd " + responseBody)
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
        //do obsługi błąd
    })
}
function renderBoards() {
    board1 = createBoard(ply1);
    board2 = createBoard(ply2);
    document.getElementById("boardPlayer1").appendChild(board1);
    document.getElementById("boardPlayer2").appendChild(board2);
}

function createBoard(diff) {
    const tableShot = document.createElement("table");
    tableShot.id ="tableBoard"+diff;
    tableShot.className = "tableShot";
    const tbodyShot = document.createElement("tbody");
    for(let i = 0; i < 10; i++) {

        const rowShot = document.createElement("tr");
        rowShot.className = "row";
        for(let j = 0; j < 10; j ++) {

            const cellShot = document.createElement("td");
            cellShot.className = "cellShot ";
            const buttonShot = document.createElement("button");
            buttonShot.className = "buttonShot " + j + "_" +i; //do elementów można przypisać wiele naz klas w tym przypadku buttonShot i j_i. Wystarczy rozdzielić te dwie nazwy spacją
            buttonShot.style.backgroundColor = "blue";
            // buttonShot.id =j+","+i+diff;
            buttonShot.addEventListener("click", function(event) {
                shotAtShip(event);
            }, false);
            cellShot.appendChild(buttonShot);

            rowShot.appendChild(cellShot);
        }
        tbodyShot.appendChild(rowShot);
    }
    tableShot.appendChild(tbodyShot);
    return tableShot;
}

function renderShot(responseBody, target) {
    console.log("ResponseBody po oddaniu strzału:  "+responseBody[0]);
    if(responseBody != null) {
        var boardShot = currentBoard === board1 ? board2 : board1;
        var index = currentBoard === board1 ? 1 : 0;
        for(const shot of responseBody[index].opponentShots) {
            // w tym warunku chciałbym wykorzystać składnię -> shot.state.equals(Shot.State.HIT)
            if(shot.state === "HIT") {
                boardShot.getElementsByClassName(shot.x + "_" +shot.y).item(0).style.backgroundColor = "red";
                // document.getElementById(shot.x+","+shot.y + ply1).style.backgroundColor = "red"
            } else if(shot.state === "MISSED") {
                var ox = boardShot.getElementsByClassName(shot.x + "_" +shot.y).item(0);// boardShot.getElementsByClassName(shot.x + "_" +shot.y) - zwraca kolekcję htmlcollection
                ox.style.backgroundColor = "black";
            }
        }
    }
    checkIfIsFinished();
}

class BattleShipClient {

    addShip(length, xstart, ystart, position, onSuccess, onError) {
        this.post("/addShip", "length=" + length + "&xstart=" + xstart + "&ystart=" + ystart + "&position=" + position, null, onSuccess, onError);
    }

    shooterShip(x, y, onSuccess, onError) {
        var shootObject = {
            x: x,
            y: y,
        }
        this.post("/json/game/boards", shootObject, null, onSuccess, onError)
    }
    getterStatusGame(onSuccess, onError) {
        this.get("/json/game/boards/isFinished", null, null, onSuccess, onError)
    }

    delete(path, body, progressUpdate, success, error) {
        this.call("delete", path, body, progressUpdate, success, error);
    }

    get(path, body, progressUpdate, success, error) {
        this.call("get", path, null, progressUpdate, success, error)
    }

    post(path, body, progressUpdate, success, error) {
        this.call("post", path, body, progressUpdate, success, error);
    }
    call(method, path, body, progressUpdate, success, error) {
        let request = new XMLHttpRequest();
        request.upload.addEventListener('progress', function (event) {
            if (event.lengthComputable) {
                let progress = event.loaded / event.total;
                if (progressUpdate != null)
                    progressUpdate(progress);
            }
        }, false);
        request.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE)
                if (this.status >= 200 && this.status <= 299) {
                    let body = this.responseText != null && this.responseText.length > 0 ? JSON.parse(this.responseText) : null; //tutaj dostaję odpowiedź z serwera tylko zamiast Json jest to html i muszę go jakos przetworzyć i podać do metody success
                    success(this.status, body);
                } else if (error !== undefined)
                    error(this.status, this.responseText);
        };
        request.open(method.toUpperCase(), path, true);
        request.setRequestHeader('Content-type', 'application/json');
        var jsonBody = JSON.stringify(body); // zmiana danych na jsona
        request.send(jsonBody);
    }
}