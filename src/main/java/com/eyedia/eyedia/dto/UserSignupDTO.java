package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.domain.enums.ExhibitionCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    private List<ExhibitionCategory> keywords;
}
