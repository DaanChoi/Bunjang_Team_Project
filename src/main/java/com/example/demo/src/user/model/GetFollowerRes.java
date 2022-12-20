package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class GetFollowerRes {
    private Integer followIdx;
    private Integer userIdx;
    private Integer followerIdx;
    private String name;
    private String profileImg;
    private Integer prdCnt;
    private Integer followerCnt;
}
