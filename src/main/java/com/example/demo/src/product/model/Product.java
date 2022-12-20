package com.example.demo.src.product.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Product {
    private String title;
    private Integer mainCategoryIdx;
    private Integer middleCategoryIdx;
    private Integer subCategoryIdx;
    private String location;
    private String productStatus;
    private Boolean isChangable;
    private String price;
    private Boolean isFreeShip;
    private String contents;
    private Integer quantity;
    private Boolean isSafepay;
}
