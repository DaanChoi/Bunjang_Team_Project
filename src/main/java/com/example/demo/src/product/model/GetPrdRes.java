package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor

public class GetPrdRes {
    private Integer productIdx;
    private String imageUrl;
    private Boolean isSafepay;
    private String title;
    private String price;
    private String location;
    private String contents;
    private Timestamp createdAt;
    private Integer favCnt;
    private Boolean isFav;
}
