package com.renukiran.annotation;

import com.renukiran.validators.TrainerCoursesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TrainerCoursesValidator.class)
public @interface TrainerMustHaveCourses {
    String message() default "Trainer must have courses";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
