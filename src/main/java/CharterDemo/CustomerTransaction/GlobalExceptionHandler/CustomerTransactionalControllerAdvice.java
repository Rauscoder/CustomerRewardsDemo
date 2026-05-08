package charterDemo.customerTransaction.globalExceptionHandler;

import charterDemo.customerTransaction.DTO.JSONErrorResponseDTO;
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
import charterDemo.customerTransaction.exception.TransactionNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class CustomerTransactionalControllerAdvice {

    // Handles JSON Mismatches (Type,Format,Mismatched Fields)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<JSONErrorResponseDTO> handleJsonErrors(HttpMessageNotReadableException ex) {
        JSONErrorResponseDTO jsonErrorResponseDTO =new JSONErrorResponseDTO();

        Map<String, String> message = new HashMap<>();
        message.put("Error", "Malformed JSON request");

        if (ex.getCause() instanceof InvalidFormatException) {
            message.put("Error Reason", "Data type mismatch or invalid date format(valid date format -dd-MM-yyyy HH:mm:ss)--"+ex.getMessage());
            log.error("Error caused due to JSON Mismatches of data type and invalid date format handled by handleJsonErrors() inside CustomerTransactionalControllerAdvice-",ex.getCause());
            log.info("Error message caused due to InvalidFormatException--{}",ex.getMessage());
        } else if (ex.getCause() instanceof UnrecognizedPropertyException) {
            message.put("Error Reason", "JSON contains unknown fields. or ExtraFields--"+ex.getMessage());
            log.error("Error caused due to unknown or extra properties present in JSON payload handled by handleJsonErrors() inside CustomerTransactionalControllerAdvice-",ex.getCause());
            log.info("Error message caused due to UnrecognizedPropertyException--{}",ex.getMessage());
        }
       jsonErrorResponseDTO.setErrorResponse(message);
        return new ResponseEntity<>(jsonErrorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    // Handles JSON Field validation  (Used For @NotNull,@NotBlank,@Size,@Pattern)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JSONErrorResponseDTO> handleJSONFieldNullValidation(MethodArgumentNotValidException ex){
        JSONErrorResponseDTO jsonErrorResponseDTO =new JSONErrorResponseDTO();
        Map<String,String> message=new HashMap<>();
        message.put("Error", "Malformed JSON request");
        message.put("Error Reason",ex.getBindingResult().toString());
        log.error("Error caused due to JSON Field Validation(like null,size,pattern etc) format handled by handleJSONFieldNullValidation() inside CustomerTransactionalControllerAdvice---",ex);
        log.info("Error Message caused due to MethodArgumentNotValidException--{}",ex.getMessage());
        jsonErrorResponseDTO.setErrorResponse(message);
        return new ResponseEntity<>(jsonErrorResponseDTO,HttpStatus.BAD_REQUEST);
    }

    // Handles DataType Mismatch For URL variable Mapping into Java object inside a Method Argument
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<JSONErrorResponseDTO> handlesPathVariableTypeMismatch(MethodArgumentTypeMismatchException ex){
        JSONErrorResponseDTO jsonErrorResponseDTO =new JSONErrorResponseDTO();
        Map<String,String> message=new HashMap<>();
        message.put("Error", "Invalid Request param ");
        message.put("Error Reason",ex.getMessage());
        log.error("Error caused due to dataType mismatch For URL variable mapping into Java object handled by handlesPathVariableTypeMismatch() inside CustomerTransactionalControllerAdvice-",ex.getCause());
        jsonErrorResponseDTO.setErrorResponse(message);
        log.info("Error message caused due to MethodArgumentTypeMismatchException--{}",ex.getMessage());
        return new ResponseEntity<>(jsonErrorResponseDTO,HttpStatus.BAD_REQUEST);
    }

    //Handles the transaction which we are saving already exists in DB
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<JSONErrorResponseDTO> handlesResponseStatusException(ResponseStatusException ex)
    {
        JSONErrorResponseDTO jsonErrorResponseDTO =new JSONErrorResponseDTO();
        Map<String,String> message=new HashMap<>();
        message.put("Error", "Invalid startDate or endDate ");
        message.put("Error Reason", ex.getMessage());
        log.info("Error message caused due to ResponseStatusException handled by handlesResponseStatusException() inside a CustomerTransactionalControllerAdvice-{}",ex.getMessage());
        jsonErrorResponseDTO.setErrorResponse(message);

        return new ResponseEntity<>(jsonErrorResponseDTO,ex.getStatusCode());
    }

    //Handles transaction not found when handling request for GET
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<JSONErrorResponseDTO> handleTransactionNotFound(TransactionNotFoundException ex)
    {
        JSONErrorResponseDTO jsonErrorResponseDTO =new JSONErrorResponseDTO();
        Map<String,String> message=new HashMap<>();
        message.put("Error", "Transaction not found for given request ");
        message.put("Error Reason", ex.getMessage());
        log.info("Error message caused due to TransactionNotFoundException handled by handleTransactionNotFound() inside a CustomerTransactionalControllerAdvice-{}",ex.getMessage());
        jsonErrorResponseDTO.setErrorResponse(message);
        return new ResponseEntity<>(jsonErrorResponseDTO,HttpStatus.NOT_FOUND);
    }

    //Handles Runtime exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<JSONErrorResponseDTO> handleRunTimeException(RuntimeException ex)
    {
        JSONErrorResponseDTO jsonErrorResponseDTO =new JSONErrorResponseDTO();
        Map<String,String> message=new HashMap<>();
        message.put("Error", "Run time errors ");
        message.put("Error Reason", "Internal Server Error");
        log.error("Error caused due to RunTimeException handled by handleRunTimeException() inside CustomerTransactionalControllerAdvice-",ex);
        log.info("Error message caused due to RunTimeException--{}",ex.getMessage());
        jsonErrorResponseDTO.setErrorResponse(message);
        return new ResponseEntity<>(jsonErrorResponseDTO,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}





