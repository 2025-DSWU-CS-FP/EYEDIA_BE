package com.eyedia.eyedia.service;

import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.domain.common.AgeUtil;
import com.eyedia.eyedia.domain.enums.Gender;
import com.eyedia.eyedia.repository.UserRepository;
import com.eyedia.eyedia.service.details.GoogleUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) {
        log.info("[OAuth] CustomOAuth2UserService.loadUser start. regId={}",
                userRequest.getClientRegistration().getRegistrationId()); // naver

        // 1) 구글 표준 OIDC 사용자 로드
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> claims = oidcUser.getClaims(); // "sub", "name", "email", "picture" 등
        String sub = (String) claims.get("sub");

        // 2) (선택) People API로 성별/출생연도 보강 — 실패해도 로그인은 계속
        Map<String, Object> people = null;
        try {
            String token = userRequest.getAccessToken().getTokenValue();
            people = GooglePeopleClient.fetch(token); // 아래 예시
        } catch (Exception e) {
            log.warn("People API failed: {}", e.toString());
        }

        // 3) 우리 도메인으로 매핑 + upsert
        GoogleUserDetails info = new GoogleUserDetails(claims, people);
        String provider = "google";
        String providerId = info.getProviderId();
        String oauthKey = provider + ":" + providerId;
        String loginId = provider + "_" + providerId;

        String name = info.getName();
        Gender gender = Gender.valueOf(info.getGender());
        Integer birthYear = info.getBirthYear();
        Integer age = AgeUtil.fromBirthYear(birthYear, ZoneId.of("Asia/Seoul"));

        User user = userRepository.findByOauthKey(oauthKey)
                .map(u -> { u.setId(loginId); u.setName(name); u.setGender(gender); u.setAge(age); return u; })
                .orElseGet(() -> User.builder()
                        .oauthKey(oauthKey)
                        .provider(provider)
                        .providerId(providerId)
                        .id(loginId)
                        .name(name)
                        .gender(gender)
                        .age(age)
                        .build());

        log.info("[OAuth] upsert {}", oauthKey);
        userRepository.save(user);
        log.info("[OAuth] saved user id={}", user.getId());

        // 4) 권한 붙여서 OIDC 유저 반환 (기존 권한 + ROLE_USER)
        List<GrantedAuthority> auth = new ArrayList<>(oidcUser.getAuthorities());
        auth.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new DefaultOidcUser(auth, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    // 아주 단순한 RestTemplate 클라이언트
    static final class GooglePeopleClient {
        @SuppressWarnings("unchecked")
        static Map<String, Object> fetch(String accessToken) {
            var rt = new org.springframework.web.client.RestTemplate();
            var headers = new org.springframework.http.HttpHeaders();
            headers.setBearerAuth(accessToken);
            var req = new org.springframework.http.HttpEntity<>(headers);
            String url = "https://people.googleapis.com/v1/people/me?personFields=genders,birthdays,names,photos";
            return rt.exchange(url, org.springframework.http.HttpMethod.GET, req, Map.class).getBody();
        }
    }
}