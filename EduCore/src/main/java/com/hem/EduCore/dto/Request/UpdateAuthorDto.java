package com.hem.EduCore.dto.Request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateAuthorDto {

    private String name;

    @Email(message = "Email must be valid")
    private String email;

    private String bio;
}
