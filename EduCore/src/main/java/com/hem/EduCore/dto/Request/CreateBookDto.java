package com.hem.EduCore.dto.Request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBookDto {



    @NotBlank
    private  String title;

    @NotBlank
    private  String description ;


    @NotBlank
    private  String pages ;

}
