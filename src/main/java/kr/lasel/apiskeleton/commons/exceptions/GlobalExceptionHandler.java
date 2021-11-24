package kr.lasel.apiskeleton.commons.exceptions;

import java.util.ArrayList;
import java.util.List;
import kr.lasel.apiskeleton.models.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({WebExchangeBindException.class})
  public Mono<ResponseEntity<String>> handleClientFault(WebExchangeBindException ex) {
    List<String> errors = new ArrayList<>();
    ex.getBindingResult().getAllErrors()
        .forEach(error -> errors.add(error.getDefaultMessage()));

    ResponseModel responseModel = new ResponseModel();
    responseModel.setData(errors);
    responseModel.setStatus(HttpStatus.BAD_REQUEST);
    responseModel.setMessage("Invalid request parameter");

    return Mono.just(responseModel.toResponse());
  }
}
