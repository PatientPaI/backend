package com.patientpal.backend.post.libs;


import com.patientpal.backend.member.domain.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleType {
    // ADMIN, USER, CAREGIVER
    Role value();

}