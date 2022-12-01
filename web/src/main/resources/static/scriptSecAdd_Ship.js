
var shipNumber;
window.onload = setup();
function setup() {
    var table;
    //zwracamy shipLimit, rozmiar planszy itp.
    // robię metode w kliencie pobierania tych danych i w tej funkcji będę miał je zwracane
    new BattleShipClient().getSetupsBoard((status, responseBody) => {
        shipNumber = responseBody;
    }, (status, responseBody) => {
        alert("Błąd przy pobieraniu ustawień " + responseBody)
    });
    table = renderShip(null);
    return  table;
}