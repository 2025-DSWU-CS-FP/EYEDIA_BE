package com.eyedia.eyedia.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserLoginResponseDTO {
    private String token;
    private boolean isFirstLogin;

    private String name;
    private Integer monthlyVisitCount;
}
