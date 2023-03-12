
class BattleShipClient {
    addShip(length, xstart, ystart, position, userId, gameId, onSuccess, onError) {
        var shipObject = {
            length: length,
            xstart: xstart,
            ystart: ystart,
            position: position
        };
        this.post("/json/addShip/"+userId+"/"+gameId, JSON.stringify(shipObject), null, onSuccess, onError);
    }
    addSecondPlayerToGame(userId, gameId, onSuccess, onError) {
        this.post("/json/addSecondPlayer/"+userId+"/"+gameId, null, null, onSuccess, onError);
    }
    getSetupsBoard(onSuccess, onError) {
        this.get("/json/setup", null, null, onSuccess, onError);
    }

    checkIfUserHasGameBefore(userId, onSuccess, onError) {
        this.get("/json/checkGames/"+userId, null, null, onSuccess, onError);
    }

    getStatusGameFromDataBase(gameId, onSuccess, onError) {
        this.get("/json/listBoard/"+gameId, null, null, onSuccess, onError);
    }

    getBoard(gameId, userId, onSuccess, onError) {
        this.get("/json/board/"+gameId+"/"+userId, null, null, onSuccess, onError);
    }


    checkIfOpponentAppears(userId, onSuccess, onError) {
        this.get("/json/opponent/"+userId, null, null, onSuccess, onError);
    }

    changeState(userId, state, onSuccess, onError) {
        this.get("/json/approve/"+userId+"/"+state, null, null, onSuccess, onError);
    }

    saverNewGame(userId, onSuccess, onError) {
        this.post("/json/game/save/"+userId, null, null, onSuccess, onError)
    }


    resumeGame(gameId, onSuccess, onError) {
        this.get("/json/resume-game/"+gameId, null, null, onSuccess, onError)
    }

    getOwnerGame(gameId, onSuccess, onError) {
        this.get("/json/owner/"+gameId, null, null, onSuccess, onError);
    }

    deleteLastAddedShip(userId, gameId, onSuccess, onError) {
        this.delete("/json/deleteShip/"+userId+"/"+gameId, null, null, onSuccess, onError);
    }

    shooterShip(x, y, gameId, onSuccess, onError) {
            var shootObject = {
                x: x,
                y: y,
            }
            this.post("/json/game/boards/"+gameId, JSON.stringify(shootObject), null, onSuccess, onError)
    }

    games(gameId, onSuccess, onError) {
        this.get("/json/games/"+currentPlayerId, null, null, onSuccess, onError);
    }

    rejectGame(userId, onSuccess, onError) {
        this.get("/json/reject/"+userId, null, null, onSuccess, onError);
    }

    getterStatusGame(userId, gameId, onSuccess, onError) {
        this.get("/json/game/status-isFinished/"+userId+"/"+gameId, null, null, onSuccess, onError);
    }


    requestJoinToGame(gameId, onSuccess, onError) {
        this.get("/json/request/"+gameId, null, null, onSuccess, onError)
    }

    deleteGame(gameId, userId, onSuccess, onError) {
        this.delete("/json/delete-game/"+userId+"/"+gameId, null, null, onSuccess, onError)
    }


    checkStatusGame(gameId, onSuccess, onError) {
        this.get("/json/status-game/"+gameId, null, null, onSuccess, onError);
    }

    checkStatusOwnGames(onSuccess, onError) {
        this.get("/json/check-state", null, null, onSuccess, onError);
    }

    updateStatusGame(userId,status, onSuccess, onError) {
        this.post("/json/update-state/"+userId, status, null, onSuccess, onError);
    }


    delete(path, body, progressUpdate, success, error) {
        this.call("delete", path, null, progressUpdate, success, error);
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
                    let body = this.responseText != null && this.responseText.length > 0 ? JSON.parse(this.responseText) : null;
                    success(this.status, body);
                } else if (error !== undefined)
                    error(this.status, this.responseText);
        };
        var token = document.querySelector("meta[name='_csrf']").getAttribute("content");
        var header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
        // csrfToken
        request.open(method.toUpperCase(), path, true);
        request.setRequestHeader(header, token);
        request.setRequestHeader('Content-type', 'application/json');
        request.send(body);
    }

}

