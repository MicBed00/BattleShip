function redirect() {
    var userName = document.getElementById("user_name").value;
    window.location.href = "/view/game?"+id;
}


function createBoard(diff) {
    const tableShot = document.createElement("table");
    tableShot.id ="tableBoard"+diff;
    tableShot.className = "tableShot";
    tableShot.style.pointerEvents = "auto";
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
            buttonShot.id = "button"+diff;
            buttonShot.addEventListener("click", function(event) {
                shotAtShip(event);
            }, true);
            cellShot.appendChild(buttonShot);

            rowShot.appendChild(cellShot);
        }
        tbodyShot.appendChild(rowShot);
    }
    tableShot.appendChild(tbodyShot);
    return tableShot;
}

