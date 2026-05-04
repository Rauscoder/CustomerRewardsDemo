package CharterDemo.CustomerTransaction.GlobalExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import CharterDemo.CustomerTransaction.util.TransactionNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class CustomerTransactionalControllerAdvice {

    // Handles JSON Mismatches (Type,Format,Mismatched Fields)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonErrors(HttpMessageNotReadableException ex) {
        Map<String, String> message = new HashMap<>();
        message.put("error", "Malformed JSON request");

        if (ex.getCause() instanceof InvalidFormatException) {
            message.put("details", "Data type mismatch or invalid date format(valid date format -dd-MM-yyyy HH:mm:ss)--"+ex.getMessage());
            log.error("Error caused due to JSON Mismatches of data type and invalid date format handled by handleJsonErrors() inside CustomerTransactionalControllerAdvice-",ex.getCause());
            log.info("Error message caused due to InvalidFormatException--{}",ex.getMessage());
        } else if (ex.getCause() instanceof UnrecognizedPropertyException) {
            message.put("details", "JSON contains unknown fields. or ExtraFields--"+ex.getMessage());
            log.error("Error caused due to unknown or extra properties present in JSON payload handled by handleJsonErrors() inside CustomerTransactionalControllerAdvice-",ex.getCause());
            log.info("Error message caused due to UnrecognizedPropertyException--{}",ex.getMessage());
        }

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

 /*   // Handles JSON Mismatches (Type,Format,Mismatched Fields)
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Map<String,String>> handleJsonFormatErrors(InvalidFormatException ex){
        Map<String,String> message=new HashMap<>();
        message.put("Error Message ",  "Invalid Date Format Kindly use DD-MM-YYYY HR:MIN:SEC format");


        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    // Handles JSON Mismatches (Type,Format,Mismatched Fields)
    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<Map<String,String>> handleJsonMismatchErrors(UnrecognizedPropertyException ex){
        Map<String,String> message=new HashMap<>();

        message.put("Error Message For Mismatched Fields",  ex.getMessage());

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
*/
    // Handles JSON Field validation  (Used For @NotNull,@NotBlank,@Size,@Pattern)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleJSONFieldNullValidation(MethodArgumentNotValidException ex){
        Map<String,String> message=new HashMap<>();
        message.put("error", "Malformed JSON request");
        message.put("Error Message For JSON Field validation",ex.getBindingResult().toString());
        log.error("Error caused due to JSON Field Validation(like null,size,pattern etc) format handled by handleJSONFieldNullValidation() inside CustomerTransactionalControllerAdvice---",ex);
        log.info("Error Message caused due to MethodArgumentNotValidException--{}",ex.getMessage());
        return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
    }

    // Handles DataType Mismatch For URL variable Mapping into Java object inside a Method Argument
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String,String>> handlesPathVariableTypeMismatch(MethodArgumentTypeMismatchException ex){
        Map<String,String> message=new HashMap<>();
        message.put("Error Message DataType Mismatch For URL variable Mapping", ex.getName()+"--"+ex.getParameter()+"--"+ex.getMessage());
        log.error("Error caused due to dataType mismatch For URL variable mapping into Java object handled by handlesPathVariableTypeMismatch() inside CustomerTransactionalControllerAdvice-",ex.getCause());
        log.info("Error message caused due to MethodArgumentTypeMismatchException--{}",ex.getMessage());
        return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
    }

    //Handles the transaction which we are saving already exists in DB
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,String>> handlesAlreadyExistingTransaction(ResponseStatusException ex)
    {
        Map<String,String> message=new HashMap<>();
        message.put("Error Message For Already Existed Customer", ex.getMessage());
        log.info("Error message caused due to ResponseStatusException handled by handlesAlreadyExistingTransaction() inside a CustomerTransactionalControllerAdvice-{}",ex.getMessage());
        return new ResponseEntity<>(message,HttpStatus.CONFLICT);
    }

    //Handles transaction not found when handling request for GET/DELETE
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleTransactionNotFound(TransactionNotFoundException ex)
    {
        Map<String,String> message=new HashMap<>();
        message.put("Error Message for TransactionNotFound", ex.getMessage());
        log.info("Error message caused due to TransactionNotFoundException handled by handleTransactionNotFound() inside a CustomerTransactionalControllerAdvice-{}",ex.getMessage());
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);
    }

    //Handles Runtime exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> handleRunTimeException(RuntimeException ex)
    {
        Map<String,String> message=new HashMap<>();
        message.put("Error Message For RunTimeException", "Internal Server Error");
        log.error("Error caused due to RunTimeException handled by handleRunTimeException() inside CustomerTransactionalControllerAdvice-",ex);
        log.info("Error message caused due to RunTimeException--{}",ex.getMessage());
        return new ResponseEntity<>(message,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
