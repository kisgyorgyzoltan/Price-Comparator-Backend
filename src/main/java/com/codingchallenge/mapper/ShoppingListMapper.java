package com.codingchallenge.mapper;

import com.codingchallenge.dto.outgoing.GetShoppingListDto;
import com.codingchallenge.model.ShoppingList;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public abstract class ShoppingListMapper {

     public abstract GetShoppingListDto toGetShoppingListDto(ShoppingList shoppingList);

    @IterableMapping(elementTargetType = GetShoppingListDto.class)
    public abstract List<GetShoppingListDto> toGetShoppingListDtos(List<ShoppingList> shoppingLists);
}
