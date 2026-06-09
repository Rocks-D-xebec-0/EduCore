package com.hem.EduCore.dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateBookDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "ISBN is required")
    @Pattern(
        regexp = "^\\d{9}[\\dXx]$|^97[89]\\d{10}$",
        message = "Invalid ISBN format. Must be ISBN-10 (e.g. 0306406152) or ISBN-13 (e.g. 9780306406157)"
    )
    private String isbn;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    private String description;

    private String publisher;

    private String publishedYear;

    @Min(value = 0, message = "Available copies must be 0 or more")
    private int availableCopies = 1;

}
