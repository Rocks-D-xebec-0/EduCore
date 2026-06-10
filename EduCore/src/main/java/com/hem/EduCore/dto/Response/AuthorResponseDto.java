package com.hem.EduCore.dto.Response;


import lombok.Data;

@Data
public class AuthorResponseDto {

    private  Long authorId;

    private  String name ;

    private  String email ;

    private  String bio ;

}
