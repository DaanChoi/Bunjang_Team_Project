package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor

public class GetPrdTagRes {
    private Integer productIdx;
    private Integer hashtagIdx;
    private String tagName;
}
