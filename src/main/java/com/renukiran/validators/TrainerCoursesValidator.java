package com.renukiran.validators;

import com.renukiran.annotation.TrainerMustHaveCourses;
import com.renukiran.dto.UserRequest;
import com.renukiran.enums.RoleType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TrainerCoursesValidator
        implements ConstraintValidator<TrainerMustHaveCourses, UserRequest> {

    @Override
    public boolean isValid(UserRequest request, ConstraintValidatorContext context) {

        if (request.getRoles() == null) return true;

        if (request.getRoles().contains(RoleType.TRAINER)) {
            if (request.getCourseIds() == null || request.getCourseIds().isEmpty()) {

                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "courseIds required when role is TRAINER"
                ).addConstraintViolation();

                return false;
            }
        }
        return true;
    }
}
