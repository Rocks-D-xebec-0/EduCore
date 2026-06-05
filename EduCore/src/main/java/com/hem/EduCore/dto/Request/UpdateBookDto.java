package com.hem.EduCore.dto.Request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateBookDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Publication year is required")
    private String publicationYear;

    @NotBlank(message = "Pages is required")
    private String pages;
}
