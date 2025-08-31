package com.eyedia.eyedia.config;

import com.eyedia.eyedia.config.jwt.JwtAuthenticationFilter;
import com.eyedia.eyedia.config.jwt.JwtProvider;
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

    public SecurityConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
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
                "https://eyedia.site"
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
                                "/api/v1/scraps/**"
                        ).permitAll()

                        // mock-detect는 인증 필요(Principal 써서 개인 큐로 보내기 때문)
                        .requestMatchers(HttpMethod.POST, "/api/v1/events/mock-detect").authenticated()

                        // 필요시 paintings는 인증 필요로 두는 걸 권장(지금은 전부 허용돼 있었음)
                        .requestMatchers("/api/v1/paintings/**").authenticated()

                        // 그 외는 인증
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
