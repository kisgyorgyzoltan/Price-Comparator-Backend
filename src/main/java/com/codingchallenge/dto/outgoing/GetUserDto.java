package com.codingchallenge.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserDto {
    private String name;
    private List<GetProductDto> shoppingCart;
    private LocalDateTime lastCartUpdate;
}
