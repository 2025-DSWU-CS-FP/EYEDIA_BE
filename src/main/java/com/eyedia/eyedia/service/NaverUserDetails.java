package com.eyedia.eyedia.service;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class NaverUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return (String) ((Map) attributes.get("response")).get("id");
    }

    @Override
    public String getName() {
        return (String) ((Map) attributes.get("response")).get("name");
    }

    @Override
    public String getGender(){
        String g = (String) ((Map) attributes.get("response")).get("gender");
        if (g == null) return null;

        return switch (g.toUpperCase()){
            case "M" -> "MALE";
            case "F" -> "FEMALE";
            default -> g;
        };
    }

    @Override
    public Integer getBirthYear() {
        String by = (String) ((Map) attributes.get("response")).get("birthyear"); // "1999" (동의 안 하면 null)
        try {
            return (by == null || by.isBlank()) ? null : Integer.parseInt(by);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
