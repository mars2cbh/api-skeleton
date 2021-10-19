package kr.lasel.apiskeleton.commons.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomException extends ResponseStatusException {

  public CustomException(HttpStatus status) {
    super(status);
  }

  public CustomException(HttpStatus status, String reason) {
    super(status, reason);
  }

  public CustomException(HttpStatus status, String reason, Throwable cause) {
    super(status, reason, cause);
  }

  public CustomException(int rawStatusCode, String reason, Throwable cause) {
    super(rawStatusCode, reason, cause);
  }
}
