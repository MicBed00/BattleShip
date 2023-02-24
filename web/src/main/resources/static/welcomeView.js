var userId = document.getElementById("user_id").value;
var title = document.getElementById("title");
var startButton = document.getElementById("startButton");
var gameSelector = document.getElementById("gameSelector");
var joinButton = document.getElementById("joinGame");
var ownGame = document.getElementById("ownGame");
var locales = document.getElementById("locales");


document.getElementById("ownGame").addEventListener("click", function () {
    document.getElementById("ownGame").setAttribute('disable', true);

    new BattleShipClient().saverNewGame(userId,(status, responseBody) => {
        if (status >= 200 && status <= 299) {
            new BattleShipClient().getGamesWaitingForUser((status, responseBody) => {

            }, (status, responseBody) => {

            })

        }

    }, (status, responseBody) => {
        alert("Błąd przy przerwaniu gry " + responseBody)
    })
}, false);

gameSelector.addEventListener('change', function() {
    if (gameSelector.selectedIndex !== -1) {
        joinButton.disabled = false;
    } else {
        joinButton.disabled = true;
    }
});

document.getElementById("joinGame").addEventListener("click", function () {

    var gameId = document.getElementById("gameSelector").value
    joinButton.disabled = true;

    new BattleShipClient().addSecondPlayerToGame(userId, gameId, (status, responseBody) => {
        alert("Udało się dodać gracza!")

    }, (status, responseBody) => {

    })

})