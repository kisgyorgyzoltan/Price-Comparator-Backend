package com.codingchallenge.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductDto {
    private String productId;
    private String productName;
    private String productCategory;
    private String brand;
    private double packageQuantity;
    private String packageUnit;
}
