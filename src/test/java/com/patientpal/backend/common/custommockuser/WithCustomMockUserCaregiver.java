package com.patientpal.backend.common.custommockuser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactoryForCaregiver.class)
public @interface WithCustomMockUserCaregiver {
    String username() default "caregiver";
    String[] roles() default {"CAREGIVER"};
}
