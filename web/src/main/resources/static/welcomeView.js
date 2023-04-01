var userId = document.getElementById("user_id").value;
var title = document.getElementById("title");
var gameSelector = document.getElementById("gameSelector");
var unfinishedGames = document.getElementById("unfinishedGames");
var joinButton = document.getElementById("joinGame");
var newGame = document.getElementById("ownGame");
var resumeGameButton = document.getElementById("resumeGame");
var deleteButton = document.getElementById("deleteGame");
var locales = document.getElementById("locales");

joinButton.addEventListener("click", requestToJoinTheGame);
newGame.addEventListener("click", createNewGame);
resumeGameButton.addEventListener("click", resumeGame);
deleteButton.addEventListener("click", deleteGame);

newGame.disabled = false;
gameSelector.disabled = false;
unfinishedGames.disabled = false;
var intervalId;
var gameIdClient;
var gameId;
intervalId = setInterval(checkIfResumeGame, 1000);

function checkIfResumeGame() {
    new BattleShipClient().checkStatusOwnGames((status, responseBody) => {
        if (status >= 200 && status <= 299) {

            var userIdGameIdList = responseBody;
            //userIdGameIdList[0] idUsera (przeciwnika), który wysyła prośbę wznowienia gry , dlatego to okno ma się wyświetlać
            if (userIdGameIdList[0] != userId && userIdGameIdList.length == 2) {
                if (confirm("Do you want resume game id " + userIdGameIdList[1] + "?") === true) {
                    // Stop the interval after 100 ms
                    setTimeout(function () {
                        clearInterval(intervalId);
                    }, 100);
                    //TU chyba muszę jeszcze wysłąć request zmieniający na stan APPROVED i dopeiro
                    approveGame();
                } else if (confirm("Do you want resume game id " + userIdGameIdList[1] + "?") === false){
                    // Stop the interval after 100 ms
                    setTimeout(function () {
                        clearInterval(intervalId);
                    }, 100);
                    rejectOpponentRequest();
                }
            }
        }
    }, (status, responseBody) => {

    })
}

function deleteGame() {
    var unfinishedGame = unfinishedGames.value;

    new BattleShipClient().deleteGame(unfinishedGame, userId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            location.reload()
        }
    }, (status, responseBody) => {
        alert("Błąd przy przerwaniu gry " + responseBody)
    })

}

function createNewGame() {
    var select = document.getElementById('gameSelector');
    select.addEventListener('change', disableAllOptions);
    newGame.disabled = true;

    new BattleShipClient().saverNewGame(userId, (status, responseBody) => {
        alert("Wait for opponent")
        gameId = responseBody;
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
            intervalId = setInterval(function () {
                waitForApprove()
            }, 1000);
        }
    }, (status, responseBody) => {
        alert("Błąd przy wysyłaniu prośby o dołączenie do gry " + responseBody)
    })
}

function waitForApprove() {
    var gameId = document.getElementById("gameSelector").value;
    new BattleShipClient().checkStatusGame(gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            if (responseBody === "APPROVED") {
                // Stop the interval after 100 ms
                setTimeout(function () {
                    clearInterval(intervalId);
                }, 100);
                joinToGame();
            } else if (responseBody === "WAITING") {
                setTimeout(function () {
                    clearInterval(intervalId);
                }, 100);
                alert("The invitation was rejected");
                gameSelector.disabled = false;
                joinButton.disabled = true;
                newGame.disabled = false;
            }
        }
    }, (status, responseBody) => {

    })
}

function waitForResumeGame() {
    var resumeGameId = unfinishedGames.value;
    new BattleShipClient().checkStatusGame(resumeGameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            if (responseBody === "APPROVED") {
                // Stop the interval after 100 ms
                setTimeout(function () {
                    clearInterval(intervalId);
                }, 100);
                new BattleShipClient().changeState(userId, "IN_PROCCESS", (status, responseBody) => {
                    if (status >= 200 && status <= 299) {
                        gameIdClient = responseBody;
                        window.location.href = "/view/getParamGame/" + resumeGameId;
                    }
                }, (status, responseBody) => {
                    alert("Błąd");
                })
            } else if (responseBody === "WAITING") {
                setTimeout(function () {
                    clearInterval(intervalId);
                }, 100);
                alert("The invitation was rejected");
                gameSelector.disabled = false;
                joinButton.disabled = true;
                newGame.disabled = false;
            }
        }
    }, (status, responseBody) => {

    })
}

//dołączenie do gry, tzn dopisanie usera do listy userów w game i dodanie gry w userze
function joinToGame() {
    var gameId = document.getElementById("gameSelector").value;
    new BattleShipClient().addSecondPlayerToGame(userId, gameId, (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            new BattleShipClient().changeState(userId, "IN_PROCCESS", (status, responseBody) => {
                if (status >= 200 && status <= 299) {
                    gameIdClient = responseBody;
                    window.location.href = "/view/getParamGame/" + gameId;
                }
            }, (status, responseBody) => {
                alert("Błąd");
            })
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

unfinishedGames.addEventListener('change', function () {
    if (unfinishedGames.selectedIndex !== -1) {
        resumeGameButton.disabled = false;
        deleteButton.disabled = false;
    } else {
        resumeGameButton.disabled = true;
        deleteButton.disabled = true;
    }
});

function checkOpponent() {
    new BattleShipClient().checkIfOpponentAppears(gameId, (status, responseBody) => {
        if (responseBody[0] === "true") {
            // Stop the interval after 100 ms
            setTimeout(function () {
                clearInterval(intervalId);
            }, 100);
            if (confirm("Player " + responseBody[1] + " wants to start to game") === true) {
                approveGame();
            } else  if (confirm("Player " + responseBody[1] + " wants to start to game") === false){
                rejectOpponentRequest();
            }
        }
    }, (status, responseBody) => {
        alert("Błąd " + responseBody)
    })
}

//rozpoczęcie gry i przekierowanie do następnego etapu
function approveGame() {
    new BattleShipClient().changeState(userId, "APPROVED", (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            gameId = responseBody;
            window.location.href = "/view/getParamGame/" + gameId;
        }
    }, (status, responseBody) => {
        alert("Błąd");
    })
}

function rejectOpponentRequest() {
    new BattleShipClient().changeState(userId, "WAITING", (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            alert("The invitation was rejected");
            intervalId = setInterval(checkOpponent, 1000);
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

function resumeGame() {
    // Stop the interval after 100 ms
    setTimeout(function () {
        clearInterval(intervalId);
    }, 100);
    new BattleShipClient().changeState(userId, "REQUESTING", (status, responseBody) => {
        if (status >= 200 && status <= 299) {
            intervalId = setInterval(waitForResumeGame, 1000);
        }
    }, (status, responseBody) => {
        alert("Błąd");
    })
}



