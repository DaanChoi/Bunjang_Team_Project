package com.example.demo.src.category.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor

public class GetCtgPrdRes {
    private Integer productIdx;
    private Timestamp createdAt;
    private String imageUrl;
    private String title;
    private String location;
    private String price;
    private Boolean isFreeShip;
    private Boolean isSafepay;
    private String tradeStatus;
    private Boolean isFav;
}
