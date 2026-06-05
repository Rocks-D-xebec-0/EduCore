package com.hem.EduCore.dto.Reponse;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberResponseDto {

    private  Long memberId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate memberShipDate;

}
