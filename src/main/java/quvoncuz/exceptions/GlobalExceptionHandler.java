package quvoncuz.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandler {

    public ResponseEntity<?> handleAlreadyExists(AlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    public ResponseEntity<?> handleDoNotMatch(DoNotMatchException e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}
