package kr.lasel.apiskeleton.commons.attributes;

import java.util.Map;
import kr.lasel.apiskeleton.commons.exceptions.CustomException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(ServerRequest request,
      ErrorAttributeOptions options) {

    Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
    errorAttributes.remove("requestId");
    errorAttributes.remove("path");
    errorAttributes.remove("timestamp");
    errorAttributes.remove("error");

    Throwable throwable = getError(request);
    if (throwable instanceof CustomException) {
      CustomException exception = (CustomException) throwable;
      errorAttributes.put("status", exception.getStatus().value());
      errorAttributes.put("message", exception.getMessage());
    }

    errorAttributes.put("message", throwable.getMessage());

    return errorAttributes;

  }

}
