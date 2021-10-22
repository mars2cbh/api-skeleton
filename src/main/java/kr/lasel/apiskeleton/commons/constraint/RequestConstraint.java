package kr.lasel.apiskeleton.commons.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import kr.lasel.apiskeleton.validator.RequestConstraintValidator;

@Documented
@Constraint(validatedBy = RequestConstraintValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestConstraint {
  String message() default "Invalid request parameter";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
