package com.example.demo.src.product.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString

public class PostPrdReq {
    private List<String> imageList;
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
    private Integer userIdx;
}
