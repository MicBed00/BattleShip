
class BattleShipClient {
    addShip(length, xstart, ystart, position, onSuccess, onError) {
        var shipObject = {
            length: length,
            xstart: xstart,
            ystart: ystart,
            position: position
        };
        this.post("/json/addShip", JSON.stringify(shipObject), null, onSuccess, onError);
    }

    getSetupsBoard(onSuccess, onError) {
        this.get("/json/setup", null, null, onSuccess, onError);
    }

    getShipId(onSuccess, onError) {
        this.get("/json/shipId", null, null, onSuccess, onError);
    }

    getShipFromDataBase(id, onSuccess, onError) {
        this.get("/json/listBoard/"+id, null, null, onSuccess, onError);
    }
    // checkifGameIsFinished(onSuccess, onError) {
    //     this.get("/json/game/boards/isFinished", null, null, onSuccess, onError);
    // }
    getShips(onSuccess, onError) {
        this.get("/json/game/boards", null, null, onSuccess, onError);
    }

    deleteLastAddedShip(idBoard,idShip, onSuccess, onError) {
        this.delete("/json/deleteShip/"+idBoard+"/"+idShip, null, null, onSuccess, onError);
    }

    shooterShip(x, y, onSuccess, onError) {
            var shootObject = {
                x: x,
                y: y,
            }
            this.post("/json/game/boards", JSON.stringify(shootObject), null, onSuccess, onError)
    }

    restoringStateBoardListOnServer(idShip, onSuccess, onError) {
        this.post("/json/setupBoard/"+idShip, null, null, onSuccess, onError);
    }

    getterStatusGame(onSuccess, onError) {
        this.get("/json/game/boards/isFinished", null, null, onSuccess, onError)
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
                    //let body = this.responseText;
                    success(this.status, body);
                } else if (error !== undefined)
                    error(this.status, this.responseText);
        };
        request.open(method.toUpperCase(), path, true);
        request.setRequestHeader('Content-type', 'application/json');
        request.send(body);
    }



}

