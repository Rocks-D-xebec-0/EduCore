package com.hem.EduCore.dto.Reponse;

import lombok.Data;

import java.util.List;

@Data
public class BookResponseDto {

    private Long id;
    private String title;
    private String isbn;
    private String description;
    private String publisher;
    private String publishedYear;
    private int availableCopies;
    private Integer categoryId;
    private String categoryName;
    private List<String> authorNames;

}
