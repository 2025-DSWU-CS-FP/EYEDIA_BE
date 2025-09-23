package com.eyedia.eyedia.config;

import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.repository.UserRepository;
import com.eyedia.eyedia.config.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Value("${app.oauth.success-redirect}")
    private String successRedirect; // e.g., https://eyedia.site/oauth/success

    @Override
    @SuppressWarnings("unchecked")
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 1) 어떤 공급자인지부터
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String regId = oauthToken.getAuthorizedClientRegistrationId(); // "google" or "naver"

        // 2) 공통 인터페이스로 받기 → OidcUser도 OAuth2User를 상속
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        // (선택) OIDC 전용 정보가 필요할 때만 체크
        OidcUser oidc = (principal instanceof OidcUser) ? (OidcUser) principal : null;

        Map<String, Object> attrs = principal.getAttributes();

        // 3) 공급자별로 providerId 추출
        String providerId;
        if ("google".equals(regId)) {
            // 구글은 평평한 JSON, 고유키는 "sub"
            providerId = (String) attrs.get("sub");
        } else if ("naver".equals(regId)) {
            // 네이버는 {response:{id:...}}
            Map<String, Object> resp = (Map<String, Object>) attrs.get("response");
            providerId = (resp == null) ? null : (String) resp.get("id");
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported provider: " + regId);
            return;
        }
        if (providerId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Provider id missing");
            return;
        }

        String oauthKey = regId + ":" + providerId;

        // 4) DB에는 CustomOAuth2UserService에서 이미 upsert 했다는 전제
        User user = userRepository.findByOauthKey(oauthKey)
                .orElseThrow(() -> new IllegalStateException("User not found after OAuth upsert: " + oauthKey));

        // 5) JWT는 숫자 PK(subject)로 발급 → parseLong 문제 방지
        String token = jwtProvider.generateToken(user.getUsersId());
        log.debug("[OAuth] JWT issued for usersId={}", user.getUsersId());

        String redirectUrl = successRedirect + "#token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}
