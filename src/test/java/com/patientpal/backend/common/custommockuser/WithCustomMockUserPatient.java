package com.patientpal.backend.common.custommockuser;

import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactoryForPatient.class)
public @interface WithCustomMockUserPatient {
    String username() default "patient";
    String[] roles() default {"USER"};
}
