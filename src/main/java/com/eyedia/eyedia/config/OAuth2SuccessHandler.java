package com.eyedia.eyedia.config;

import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.repository.UserRepository;
import com.eyedia.eyedia.config.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
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
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> resp = (Map<String, Object>) oAuth2User.getAttributes().get("response");

        String providerId = (String) resp.get("id");
        String oauthKey = "naver:" + providerId;

        User user = userRepository.findByOauthKey(oauthKey)
                .orElseThrow(() -> new IllegalStateException("로그인 직후 사용자 없음: " + oauthKey));

        String token = jwtProvider.generateToken(user.getUsersId());
        String redirectUrl = successRedirect + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}
