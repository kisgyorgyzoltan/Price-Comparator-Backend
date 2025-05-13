package com.codingchallenge.mapper;

import com.codingchallenge.dto.incoming.CreateDiscountEntryDto;
import com.codingchallenge.dto.outgoing.GetDiscountEntryDto;
import com.codingchallenge.model.DiscountEntry;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiscountEntryMapper {
    GetDiscountEntryDto toGetDiscountEntryDto(DiscountEntry discountEntry);

    @IterableMapping(elementTargetType = GetDiscountEntryDto.class)
    List<GetDiscountEntryDto> toGetDiscountEntryDtos(List<DiscountEntry> discountEntries);

    DiscountEntry toDiscountEntry(CreateDiscountEntryDto createDiscountEntryDto);
}
