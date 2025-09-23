package com.eyedia.eyedia.service;

public interface OAuth2UserInfo {

    String getProvider();    // "naver"
    String getProviderId();  // 네이버 id
    String getName();
    Integer getBirthYear();
    String getGender();      // "M"/"F"
}
