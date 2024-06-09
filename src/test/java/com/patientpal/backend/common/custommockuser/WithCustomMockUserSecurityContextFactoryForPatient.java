package com.patientpal.backend.common.custommockuser;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockUserSecurityContextFactoryForPatient implements WithSecurityContextFactory<WithCustomMockUserPatient> {
    @Override
    public SecurityContext createSecurityContext(WithCustomMockUserPatient customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserDetails principal = User.builder()
                .username(customUser.username())
                .password("password") // 실제로 사용되지 않음
                .authorities(Arrays.stream(customUser.roles())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()))
                .build();

        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities()));

        return context;
    }
}
