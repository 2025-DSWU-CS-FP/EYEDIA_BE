package com.eyedia.eyedia.service.details;

import com.eyedia.eyedia.domain.enums.Gender;

public interface OAuth2UserInfo {

    String getProvider();    // "naver"
    String getProviderId();  // 네이버 id
    String getName();
    Integer getBirthYear();
    String getGender();      // "M"/"F"
}
