package com.eyedia.eyedia.service;

import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.domain.common.AgeUtil;
import com.eyedia.eyedia.domain.enums.Gender;
import com.eyedia.eyedia.repository.UserRepository;
import com.eyedia.eyedia.service.details.NaverUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        log.info("[OAuth] CustomOAuth2UserService.loadUser start. regId={}",
                userRequest.getClientRegistration().getRegistrationId()); // naver

        OAuth2User raw = super.loadUser(userRequest);

        Map<String, Object> attributes = raw.getAttributes();

        NaverUserDetails n = new NaverUserDetails(attributes);

        String provider = n.getProvider();              // "naver"
        String providerId = n.getProviderId();         // naver id
        String oauthKey = provider + ":" + providerId; // unique key

        String loginId = provider + "_" + providerId;
        String name = n.getName();
        Gender gender = parseGender(n.getGender());
        Integer birthYear = n.getBirthYear();
        Integer age = AgeUtil.fromBirthYear(birthYear, ZoneId.of("Asia/Seoul"));

        User user = userRepository.findByOauthKey(oauthKey)
                .map(u -> {
                    u.setId(loginId);
                    u.setName(name);
                    u.setGender(gender);
                    u.setAge(age);
                    return u;
                })
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

        // Security Context용 OAuth2User (ROLE_USER 1개)
        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return new DefaultOAuth2User(authorities, attributes, "response");
    }

    private Gender parseGender(String g) {
        if (g == null) return null;
        String s = g.trim().toUpperCase(Locale.ROOT);
        if ("M".equals(s) || "MALE".equals(s)) return Gender.MALE;
        if ("F".equals(s) || "FEMALE".equals(s)) return Gender.FEMALE;
        return null; // treat "U"/others as unknown
    }
}