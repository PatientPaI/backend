package com.patientpal.backend.post.libs;


import com.patientpal.backend.common.exception.AuthenticationException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RoleInterceptor implements HandlerInterceptor {
    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RoleType roleType = handlerMethod.getMethod().getAnnotation(RoleType.class);

            if (roleType == null) {
                return true;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserDetails) {
                    String userName = ((UserDetails) principal).getUsername();
                    Member member = memberService.getUserByUsername(userName);

                    if (member.getRole() == Role.ADMIN) {
                        return true;
                    }

                    if (roleType.value() != member.getRole()) {
                        throw new AuthenticationException(ErrorCode.AUTHORIZATION_FAILED);
                    }
                }
            }
        }
        return true;
    }
}
