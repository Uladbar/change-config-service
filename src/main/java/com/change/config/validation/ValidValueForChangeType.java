package com.change.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidChangeValueForChangeTypeValidator.class)
public @interface ValidValueForChangeType {

  String message() default "Invalid field combination";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
