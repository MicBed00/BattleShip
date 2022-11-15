

function adderShip(event) {

    var length = document.getElementById("len").value;
    var position = document.getElementById("orientation").value;
    var xstart;
    var target = event.target, col, row;

    col = target.parentElement;
    row = col.parentElement;
    ystart = row.rowIndex;
    xstart = col.cellIndex;

    new BattleShipClient().addShip(length, xstart, ystart, position, (status, responseBody) => {
        //ten warunek chyba jest niepotrzebny bo numer status sprawdzam w call
        if (status >= 200 && status <= 299) {
            //reset parametrów do domyślnych
            document.getElementById("len").value = 1;
            document.getElementById("orientation").value = "VERTICAL";

            //wymyślić inny warunek dla odblokowania przycisku
            if(responseBody[0].ships.length <= shipNumber && responseBody[1].ships.length === 0) {
                renderShip(responseBody[0])

                if(responseBody[0].ships.length === shipNumber) {
                    document.getElementById("renderTable").style.pointerEvents = "none";
                    document.getElementById("accept").disabled = false;
                    document.getElementById("accept").addEventListener("click", function () {
                        renderShip(null);
                        document.getElementById("renderTable").style.pointerEvents = "auto";
                        document.getElementsByClassName("button").disabled = false;
                        document.getElementById("accept").disabled = true;
                    },false);
                }
            } else if(responseBody[0].ships.length === shipNumber && responseBody[1].ships.length <= shipNumber) {
                renderShip(responseBody[1])

                if(responseBody[1].ships.length === shipNumber) {
                    document.getElementById("renderTable").style.pointerEvents = "none";
                    document.getElementById("accept").disabled = false;

                    document.getElementById("accept").addEventListener("click", function () {
                        document.getElementById("accept").disabled = true;
                        window:location.replace("http://localhost:8080/view/added_Ship");
                    },false);
                }
            }
        }

    }, (status, responseBody) => {
        if (status >= 400 && status <= 499) {
            alert(responseBody);
            //Uwaga: sprawdź czy po wyrzuceniu błędu statek mimo to nie jest dodawny do listy!
        }
    });
}

function renderShip(responseBody) {
    var horizontalOrientation = [[${orientList}]][1];
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
            button.id = j+"&"+i
            button.addEventListener("click", function(event) {
                adderShip(event);
            }, false);
            cell.appendChild(button);

            if(responseBody != null) {
                for(let k = 0; k < responseBody.ships.length; k ++) {
                    if (responseBody.ships[k].position === horizontalOrientation) {

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
//
// class BattleShipClient {
//     addShip(length, xstart, ystart, position, onSuccess, onError) {
//         var shipObject = {
//             length: length,
//             xstart: xstart,
//             ystart: ystart,
//             position: position
//         };
//         this.post("/json/addShip", JSON.stringify(shipObject), null, onSuccess, onError);
//     }
//
//     getSetupsBoard(onSuccess, onError) {
//         this.get("/json/setup", null, null, onSuccess, onError);
//     }
//
//     shooterShip(x, y, onSuccess, onError) {
//         this.post("/game/boards", "x=" + x + "&y="+ y, null, onSuccess, onError)
//     }
//     getterStatusGame(onSuccess, onError) {
//         this.get("/game/boards/isFinished", null, null, onSuccess, onError)
//     }
//
//     delete(path, body, progressUpdate, success, error) {
//         this.call("delete", path, body, progressUpdate, success, error);
//     }
//
//     get(path, body, progressUpdate, success, error) {
//         this.call("get", path, null, progressUpdate, success, error)
//     }
//
//     post(path, body, progressUpdate, success, error) {
//         this.call("post", path, body, progressUpdate, success, error);
//     }
//
//     call(method, path, body, progressUpdate, success, error) {
//         let request = new XMLHttpRequest();
//         request.upload.addEventListener('progress', function (event) {
//             if (event.lengthComputable) {
//                 let progress = event.loaded / event.total;
//                 if (progressUpdate != null)
//                     progressUpdate(progress);
//             }
//         }, false);
//
//         request.onreadystatechange = function () {
//             if (this.readyState === XMLHttpRequest.DONE)
//                 if (this.status >= 200 && this.status <= 299) {
//                     let body = this.responseText != null && this.responseText.length > 0 ? JSON.parse(this.responseText) : null; //tutaj dostaję odpowiedź z serwera tylko zamiast Json jest to html i muszę go jakos przetworzyć i podać do metody success
//                     //let body = this.responseText;
//                     success(this.status, body);
//                 } else if (error !== undefined)
//                     error(this.status, this.responseText);
//         };
//         request.open(method.toUpperCase(), path, true);
//         request.setRequestHeader('Content-type', 'application/json');
//         request.send(body);
//     }
// }
