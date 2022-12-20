package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor

public class GetUserPrdRes {
    private Integer productIdx;
    private Integer userIdx;
    private Boolean isSafepay;
    private String tradeStatus;
    private String imageUrl;
    private String title;
    private String price;
    private String location;
    private Timestamp createdAt;
    private Boolean isFav;
}
