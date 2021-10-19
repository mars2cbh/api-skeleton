package kr.lasel.apiskeleton.models;

import kr.lasel.apiskeleton.commons.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.HashMap;

public class ResponseModel {

  @Getter
  @Setter
  private HttpStatus status;
  @Getter
  @Setter
  private String message;
  @Getter
  @Setter
  private Object data;

  long startTime;

  public ResponseModel() {
    reset();
  }

  public void reset() {
    this.status = HttpStatus.OK;
    this.message = "Success";
    this.data = null;
    startTime = System.currentTimeMillis();
  }

  public ResponseEntity<Mono<String>> toResponse() {
    return ResponseEntity
        .status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(toJson()));
  }

  public String toJson() {

    long endTime = System.currentTimeMillis();

    HashMap<String, Object> result = new HashMap<>();

    result.put("status", status.value());
    result.put("message", message);

    if (data != null) {
      result.put("data", data);
    }

    result.put("executeTime", (endTime - startTime));

    return JsonUtils.convertObjectToJsonString(result);

  }

}
