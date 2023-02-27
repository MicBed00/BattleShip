var userId = document.getElementById("user_id").value;
var title = document.getElementById("title");
var gameSelector = document.getElementById("gameSelector");
var joinButton = document.getElementById("joinGame");
var newGame = document.getElementById("ownGame");
var locales = document.getElementById("locales");
newGame.disabled = false;
gameSelector.disabled = false;
document.getElementById("joinGame").addEventListener("click", requestToJoinTheGame);
document.getElementById("ownGame").addEventListener("click", createNewGame);
//zmienna intervalId
var intervalId;
var gameIdClient;

function createNewGame() {
    var select = document.getElementById('gameSelector');
    select.addEventListener('change', disableAllOptions);
    newGame.disabled = true;

    new BattleShipClient().saverNewGame(userId, (status, responseBody) => {
        alert("Wait for opponent")
        intervalId = setInterval(checkOpponent, 1000)
    }, (status, responseBody) => {
        alert("Błąd przy przerwaniu gry " + responseBody)
    })
}

function requestToJoinTheGame() {
    gameSelector.addEventListener('change', disableAllOptions);
    gameSelector.disabled = true;
    joinButton.disabled = true;
    newGame.disabled = true;

    var gameId = document.getElementById("gameSelector").value;

    new BattleShipClient().requestJoinToGame(gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            gameIdReq = responseBody;
            intervalId = setInterval(waitForApprove(gameIdReq), 1000);

        }
    }, (status, responseBody) => {
        alert("Błąd przy wysyłaniu prośby o dołączenie do gry " + responseBody)
    })
}

function waitForApprove(gameId) {
    //TODO muszę zrobić endpoit na któym będzie nasłuchiwał zaminy statusu gry z requesting na approved metoda checkStatusGame
    // var gameId = document.getElementById("gameSelector").value;

    new BattleShipClient().checkStatusGame(gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            alert("Jestem w check status")
            if (responseBody === "APPROVED")
                // Stop the interval after 100 ms
                setTimeout(function () {
                    clearInterval(intervalId);
                }, 100);
            joinToGame();

        } else if (responseBody === "REJECTED") {
            //TODO zmiana statusu na WAITING i  info o odmowie dostępu do gry
        }
    }, (status, responseBody) => {

    })
}

function joinToGame() {
    var gameId = document.getElementById("gameSelector").value;
    new BattleShipClient().addSecondPlayerToGame(userId, gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            gameIdClient = responseBody;
            window.location.href = "/view/getParamGame/" + gameId;
        }
    }, (status, responseBody) => {
        alert("Błąd przy dodawaniu gry " + responseBody)
    })
}

gameSelector.addEventListener('change', function () {
    if (gameSelector.selectedIndex !== -1) {
        joinButton.disabled = false;
    } else {
        joinButton.disabled = true;
    }
});

function checkOpponent() {
    new BattleShipClient().checkIfOpponentAppears(userId, (status, responseBody) => {
        if (responseBody[0] === "true") {
            // Stop the interval after 100 ms
            setTimeout(function () {
                clearInterval(intervalId);
            }, 100);

            if (confirm("Player " + responseBody[1] + " wants to start to game") === true) {
                approveGame();
            } else {
                //TODO wysłanie requestu z odrzuceniem
                txt = "Rejected player!";
            }

        }
    }, (status, responseBody) => {
        alert("Błąd " + responseBody)
    })
}

function approveGame() {
    new BattleShipClient().approveGame(userId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            gameId = responseBody;
            window.location.href = "/view/getParamGame/" + gameId;
        }
    }, (status, responseBody) => {
        alert("Błąd");
    })
}

function disableAllOptions() {
    var options = this.options;
    for (var i = 0; i < options.length; i++) {
        if (options[i].selected) {
            continue;
        }
        options[i].disabled = true;
    }
}

function activeAllOptions() {
    var options = this.options;
    for (var i = 0; i < options.length; i++) {
        options[i].disabled = false;
    }
}


//TODO zapytanie czy wznawiamy ostatnio zapisaną grę???
// function setup() {
//     var table;
//
//     new BattleShipClient().getSetupsBoard((status, responseBody) => {
//         shipNumber = responseBody;
//     }, (status, responseBody) => {
//         alert("Błąd przy pobieraniu ustawień " + responseBody)
//     });
//
//
//     new BattleShipClient().checkIfUserHasGameBefore(userId,(status, responseBody) => {
//         let gameExistForUser;
//         if (status >= 200 && status <= 299) {
//             gameExistForUser = responseBody;
//
//             if (gameExistForUser === true) {
//                 //zamiast idStatus musi być userId z contextu bezpieczeństwa wyciągniętego w HTML
//                 new BattleShipClient().getPhaseGame(userId, (status, responseBody) => {
//                     if (status >= 200 && status <= 299) {
//
//                         var gameOver = responseBody //tu wyciągam wartość pola 'state' ze statusu rozgrywki
//
//                         if (gameOver === 'FINISHED' || gameOver === 'REJECTED') {
//
//                         } else {
//                             document.getElementById("id_resumeGame").hidden = false;
//                         }
//                     }
//                 }, (status, responseBody) => {
//                     alert("Błąd przy sprawdzaniu statusu gry " + responseBody);
//                 });
//
//             } else {
//                 //user nie miał jeszcze gry w tabeli games, która miałby zapisany stan gry w tabeli games_statuses
//                 // dlatego tu tworzymy nowa grę w games
//                 new BattleShipClient().saverNewGame(userId,(status, responseBody) => {
//                     if (status >= 200 && status <= 299) {
//                         table = renderShip(null);
//                         return table;
//                     }
//                 }, (status, responseBody) => {
//                     alert("Błąd przy zapisywaniu nowej gry " + responseBody);
//                 })
//             }
//         }
//     }, (status, responseBody) => {
//         alert("Błąd przy wznawianiu gry " + responseBody);
//     });


