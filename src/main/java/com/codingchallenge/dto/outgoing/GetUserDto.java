package com.codingchallenge.dto.outgoing;

import com.codingchallenge.model.User.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserDto {
    private String id;
    private String name;
    private List<CartItem> shoppingCart;
    private LocalDateTime lastCartUpdate;
}
