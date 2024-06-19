package com.patientpal.backend.post.libs;


import com.patientpal.backend.member.domain.Role;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Repeatable(value = RoleTypes.class)
@Retention(RetentionPolicy.RUNTIME)

public @interface RoleType {
    // ADMIN, USER, CAREGIVER
    Role value();
}