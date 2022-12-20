package com.example.demo.src.category.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class GetCtgRes {
    private Integer mainCategoryIdx;
    private String mainCategoryName;
    private Integer middleCategoryIdx;
    private String middleCategoryName;
    private Integer subCategoryIdx;
    private String subCategoryName;
}
