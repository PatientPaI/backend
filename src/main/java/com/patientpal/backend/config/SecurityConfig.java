package com.patientpal.backend.config;

import com.patientpal.backend.security.jwt.JwtAccessDeniedHandler;
import com.patientpal.backend.security.jwt.JwtAuthTokenFilter;
import com.patientpal.backend.security.jwt.JwtAuthenticationEntryPoint;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import com.patientpal.backend.security.oauth.CustomOauth2UserService;
import java.util.Collections;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CustomOauth2UserService customOauth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        return http
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
            // .oauth2Login(oauth -> oauth
            //         .userInfoEndpoint(userInfo -> userInfo
            //                 .userService(customOauth2UserService))
            //         .authorizationEndpoint(authorization -> authorization
            //                 .baseUri("/login/oauth2/authorize"))
            //         .redirectionEndpoint(redirection -> redirection
            //                 .baseUri("/login/oauth2/code/{code}"))
            .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setExposedHeaders(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            return config;
        };
    }
}
