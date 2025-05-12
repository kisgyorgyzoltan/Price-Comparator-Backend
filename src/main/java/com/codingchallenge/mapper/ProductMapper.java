package com.codingchallenge.mapper;

import com.codingchallenge.dto.incoming.CreateProductDto;
import com.codingchallenge.dto.outgoing.GetProductDto;
import com.codingchallenge.model.Product;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    GetProductDto toGetProductDto(Product product);

    @IterableMapping(elementTargetType = GetProductDto.class)
    List<GetProductDto> toGetProductDtos(List<Product> products);

    Product toProduct(CreateProductDto createProductDto);
}
