package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor

public class GetPrdDetailRes {
    private Integer productIdx;
    private String price;
    private String title;
    private String location;
    private Timestamp createdAt;
    private String productStatus;
    private Integer quantity;
    private Boolean isFreeShip;
    private Boolean isChangable;
    private Integer favCnt;
    private Integer chatCnt;
    private String contents;
    private Boolean isSafepay;
    private Boolean isFav;
}
