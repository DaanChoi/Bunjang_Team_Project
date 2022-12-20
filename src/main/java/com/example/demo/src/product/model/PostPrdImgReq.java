package com.example.demo.src.product.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString


public class PostPrdImgReq {
    private Integer productIdx;
    private List<String> imageList;
}
