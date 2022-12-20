package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor

public class GetFollowingRes {
    private Integer followIdx;
    private Integer userIdx;
    private Integer followingIdx;
    private String name;
    private String profileImg;
    private Integer prdCnt;
    private Integer followerCnt;
}
