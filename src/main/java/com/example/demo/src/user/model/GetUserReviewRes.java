package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor

public class GetUserReviewRes {
    private Integer reviewIdx;
    private double rate;
    private String contents;
    private String title;
    private Integer reviewerIdx;
    private String name;
    private Timestamp createdAt;
    private Integer revieweeIdx;
    private Integer productIdx;
}
