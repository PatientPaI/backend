package com.patientpal.backend.config;

import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.security.jwt.JwtAccessDeniedHandler;
import com.patientpal.backend.security.jwt.JwtAuthTokenFilter;
import com.patientpal.backend.security.jwt.JwtAuthenticationEntryPoint;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import com.patientpal.backend.security.oauth.CustomOauth2UserPrincipal;
import com.patientpal.backend.security.oauth.CustomOauth2UserService;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CustomOauth2UserService customOauth2UserService;
    private final OAuth2ClientConfig oAuth2ClientConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http, MemberService memberService) throws Exception {
         http
            .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(configurer -> configurer.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .addFilterBefore(new JwtAuthTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // TODO: 향후 OAuth2 로그인 기능을 이어서 구현해야 함
            .oauth2Login(oauth -> oauth
                    .clientRegistrationRepository(oAuth2ClientConfig.clientRegistrationRepository())
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(customOauth2UserService))
                    .authorizationEndpoint(authorization -> authorization
                            .baseUri("/login/oauth2/authorize"))
                    .redirectionEndpoint(redirection -> redirection
                            .baseUri("/login/oauth2/code/{code}"))

                    .successHandler((request, response, authentication) -> {
                        CustomOauth2UserPrincipal userPrincipal = (CustomOauth2UserPrincipal) authentication.getPrincipal();
                        Map<String, Object> attributes = userPrincipal.getAttributes();

                        String registrationId = (String) attributes.get("registrationId");
                        String email = null;
                        String name = null;
                        Role role = Role.USER;

                        if ("google".equals(registrationId)) {
                            email = (String) attributes.get("email");
                            name = (String) attributes.get("name");
                        } else if ("kakao".equals(registrationId)) {
                            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                            Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
                            email = (String) kakaoAccount.get("email");
                            name = (String) kakaoProfile.get("nickname");
                        } else if ("naver".equals(registrationId)) {
                            email = (String) attributes.get("email");
                            name = (String) attributes.get("name");
                        }

                        String redirectUri = (String) request.getSession().getAttribute(REDIRECT_URI_PARAM_COOKIE_NAME);
                        if (redirectUri == null) {
                            redirectUri = "/";
                        }

                        if (email != null && memberService.existsByUsername(email)) {
                            String token = jwtTokenProvider.createAccessToken(authentication);
                            response.addHeader("Authorization", "Bearer " + token);
                            response.sendRedirect(redirectUri);
                        } else {
                            HttpSession session = request.getSession();
                            session.setAttribute("email", email);
                            session.setAttribute("name", name);
                            session.setAttribute("role", role);
                            session.setAttribute("provider", registrationId);
                            String username = createUniqueUsername(email, memberService);
                            session.setAttribute("username", username);
                            response.sendRedirect("/api/v1/auth/oauth2/register");
                        }
                    })
            )
                 .logout(logout -> logout
                         .logoutUrl("/logout")
                         .logoutSuccessUrl("/")
                         .invalidateHttpSession(true)
                         .deleteCookies("JSESSIONID", "refresh_token"));

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://patientpal.site"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setExposedHeaders(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            return config;
        };
    }

    private String createUniqueUsername(String baseName, MemberService memberService) {
        List<String> existingUsernames = memberService.findUsernamesStartingWith(baseName);
        String username = baseName;
        int count = 1;
        while (existingUsernames.contains(username)) {
            username = baseName + getHash(count);
            count++;
        }
        return username;
    }

    private String getHash(int count) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(String.valueOf(count).getBytes());
            return bytesToHex(hash).substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
