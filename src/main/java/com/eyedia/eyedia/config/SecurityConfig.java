package com.eyedia.eyedia.config;

import com.eyedia.eyedia.config.jwt.JwtAuthenticationFilter;
import com.eyedia.eyedia.config.jwt.JwtProvider;
import com.eyedia.eyedia.service.CustomOAuth2UserService;
import com.eyedia.eyedia.service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// CORS for Spring Security
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(JwtProvider jwtProvider,
                          CustomOAuth2UserService customOAuth2UserService,
                          CustomOidcUserService customOidcUserService,
                          OAuth2SuccessHandler oAuth2SuccessHandler) {

        this.jwtProvider = jwtProvider;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOidcUserService = customOidcUserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    // Spring Security가 인식하는 CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        // 개발 편의상 넓게 허용(운영에서는 도메인 제한 권장)
        c.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",   // Vite dev
                "http://localhost:3000",
                "http://localhost:8000",
                "http://localhost:8080",
                "https://eyedia.netlify.app",
                "https://eyedia.site",
                "http://3.34.240.201:8000"
        ));
        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        c.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With","Accept"));
        c.setAllowCredentials(true);
        c.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 이 CORS는 위의 corsConfigurationSource()를 사용
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 공개 엔드포인트
                        .requestMatchers(
                                "/api/v1/auth/signup",
                                "/api/v1/auth/login",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/api/v1/ai/**",
                                "/chat/send-ai-message",
                                "/ws-stomp", "/ws-stomp/**",
                                "/", "/health-check",
                                "/api/v1/paintings/**",
                                "/api/v1/scraps/**",
                                "/tts/**",
                                "/api/v1/events/detect",
                                "/api/v1/events/detect-area",
                                "/api/v1/chats/ask",

                                // OAuth2 로그인 경로 허용
                                "/oauth2/**", "/login/oauth2/**", "/oauth2/authorization/**"
                        ).permitAll()

                        // 그 외는 인증
                        .anyRequest().authenticated()
                )

                // 네이버 OAuth2 로그인 파이프라인
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(ui -> ui
                                .userService(customOAuth2UserService)
                                .oidcUserService(customOidcUserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                )

                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
