package com.codingchallenge.dto.outgoing;

import com.codingchallenge.dto.internal.BestProductPriceResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetShoppingListDto {
    private String id;
    private String userId;
    private String name;
    private LocalDate createdDate;
    private HashMap<String, List<BestProductPriceResult>> products;
}
