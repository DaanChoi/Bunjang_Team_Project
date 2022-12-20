package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor

public class GetUserFavRes {
    private Integer favoriteIdx;
    private Integer userIdx;
    private Integer favoriteProductIdx;
    private String tradeStatus;
    private Boolean isSafepay;
    private String imageUrl;
    private String title;
    private String price;
    private Integer sellerIdx;
    private String name;
    private Timestamp createdAt;
    private Boolean isFav;
}
