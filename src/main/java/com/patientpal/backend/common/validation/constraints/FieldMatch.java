package com.patientpal.backend.common.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

import com.patientpal.backend.common.validation.FieldMatchValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldMatchValidator.class)
public @interface FieldMatch {
    String message() default "{com.patientpal.backend.common.validation.constraints.FieldMatch.message}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    /**
     * @return 매치되어야 하는 첫 번째 필드 이름
     */
    String first();

    /**
     * @return 매치되어야 하는 두 번째 필드 이름
     */
    String second();

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        FieldMatch[] value();
    }
}
