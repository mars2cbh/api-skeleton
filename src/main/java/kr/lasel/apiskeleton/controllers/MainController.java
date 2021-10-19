package kr.lasel.apiskeleton.controllers;

import kr.lasel.apiskeleton.commons.exceptions.CustomException;
import kr.lasel.apiskeleton.models.ResponseModel;
import kr.lasel.apiskeleton.services.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class MainController {

  private final MainService mainService;

  public MainController(MainService mainService) {
    this.mainService = mainService;
  }

  @GetMapping("/")
  public ResponseEntity<Mono<String>> index() {

    ResponseModel responseModel = new ResponseModel();

    HttpStatus status = HttpStatus.OK;

    responseModel.setStatus(status);
    responseModel.setMessage(status.getReasonPhrase());
    responseModel.setData(mainService.getHello());

    return responseModel.toResponse();

  }

  @GetMapping("/error")
  public ResponseEntity<Mono<String>> error() {
    throw new CustomException(HttpStatus.PRECONDITION_FAILED,
        HttpStatus.PRECONDITION_FAILED.getReasonPhrase());
  }

}
