package com.hem.EduCore.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CreateMemberDto{

    @NotBlank
    private  String name ;

    @Email
    @NotBlank
    private  String email ;

    private  String phone ;

    @NotBlank
    private  String address ;

}
