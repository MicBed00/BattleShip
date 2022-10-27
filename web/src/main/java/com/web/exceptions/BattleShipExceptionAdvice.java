package com.web.exceptions;

import exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BattleShipExceptionAdvice {


    @ExceptionHandler(BattleShipException.class)
    public ResponseEntity<String> battleShipExceptionHandler(BattleShipException ex) {
        if(ex instanceof CollidingException || ex instanceof ShotSamePlaceException || ex instanceof ShipLimitExceedException)
            return new ResponseEntity<>(ex.toString(), HttpStatus.FORBIDDEN);
        if(ex instanceof NullObject)
            return new ResponseEntity<>(ex.toString(), HttpStatus.NO_CONTENT);
        if(ex instanceof OutOfBoundsException)
            return new ResponseEntity<>(ex.toString(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(ex.toString(), HttpStatus.NOT_FOUND);
    }


}
