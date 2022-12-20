package com.example.demo.src.product.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Favorite {
    private Integer favoriteIdx;
    private String status;
    private Integer userIdx;
    private Integer productIdx;
}
