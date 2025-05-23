package com.eyedia.eyedia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupDTO {

    private String name;
    private Integer age;
    private String gender;
    private String profileImage;
    private String id;
    private String pw;
    private String currentLocation;

}
