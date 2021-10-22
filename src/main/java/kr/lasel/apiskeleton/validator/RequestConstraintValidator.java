package kr.lasel.apiskeleton.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import kr.lasel.apiskeleton.commons.constraint.RequestConstraint;
import kr.lasel.apiskeleton.models.RequestModel;

public class RequestConstraintValidator implements
    ConstraintValidator<RequestConstraint, RequestModel> {

  @Override
  public boolean isValid(RequestModel value, ConstraintValidatorContext context) {

    if (!value.getName().equalsIgnoreCase("david")) {

      context.buildConstraintViolationWithTemplate("Only the name david is allowed.")
          .addConstraintViolation();

      return false;
    }

    return true;
  }
}
