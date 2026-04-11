package com.renukiran.validators;

import com.renukiran.annotation.TrainerMustHaveCourses;
import com.renukiran.dto.UserRequest;
import com.renukiran.enums.RoleType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TrainerCoursesValidator
        implements ConstraintValidator<TrainerMustHaveCourses, UserRequest> {

    @Override
    public boolean isValid(UserRequest dto, ConstraintValidatorContext context) {

        if (dto.getRole() == null) return true;

        if (dto.getRole() == RoleType.TRAINER) {

            if (dto.getCourseIds() == null || dto.getCourseIds().isEmpty()) {

                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "courseIds required when role is TRAINER"
                        ).addPropertyNode("courseIds")
                        .addConstraintViolation();

                return false;
            }
        }

        return true;
    }
}
