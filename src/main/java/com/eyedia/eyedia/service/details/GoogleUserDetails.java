package com.eyedia.eyedia.service.details;

import com.eyedia.eyedia.domain.enums.Gender;
import lombok.AllArgsConstructor;

import java.util.Map;

import static com.eyedia.eyedia.domain.enums.Gender.*;

@AllArgsConstructor
public class GoogleUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes;
    private Map<String, Object> people;

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getGender() {
        String raw = extractGenderFromPeople(people);
        if (raw == null && attributes.get("gender") instanceof String s && !s.isBlank()) raw = s;

        return normalizeGender(raw); // "MALE" / "FEMALE" / "UNKNOWN"
    }

    private static String normalizeGender(String g) {
        if (g == null) return "NON";
        switch (g.trim().toLowerCase()) {
            case "m": case "male":   return "MALE";
            case "f": case "female": return "FEMALE";
            default:                 return "NON"; // other, unspecified 등
        }
    }

    @SuppressWarnings("unchecked")
    private static String extractGenderFromPeople(Map<String, Object> people) {
        if (people == null) return null;
        Object gendersObj = people.get("genders");
        if (!(gendersObj instanceof java.util.List<?> list) || list.isEmpty()) return null;

        Map<String, Object> chosen = null;
        for (Object o : list) {
            if (o instanceof Map<?,?> m) {
                Object metaObj = m.get("metadata");
                if (metaObj instanceof Map<?,?> meta) {
                    Object p = meta.get("primary");
                    if (p instanceof Boolean b && b) { chosen = (Map<String,Object>) m; break; }
                }
                if (chosen == null) chosen = (Map<String, Object>) m;
            }
        }

        if (chosen == null) return null;
        Object v = chosen.get("value"); // "male"/"female"/"other"
        if (v instanceof String s && !s.isBlank()) return s;
        Object fv = chosen.get("formattedValue");
        return (fv instanceof String fs && !fs.isBlank()) ? fs : null;
    }

    @Override
    public Integer getBirthYear() {
        if (people == null) return null;
        Object birthdays = people.get("birthdays");
        if (birthdays instanceof java.util.List<?> list) {
            for (Object o : list) {
                if (o instanceof Map<?,?> m) {
                    Object date = m.get("date");
                    if (date instanceof Map<?,?> d) {
                        Object year = d.get("year");
                        if (year instanceof Number num) return num.intValue();
                        if (year instanceof String s) {
                            try { return Integer.parseInt(s); } catch (Exception ignore) {}
                        }
                    }
                }
            }
        }
        return null;
    }
}
