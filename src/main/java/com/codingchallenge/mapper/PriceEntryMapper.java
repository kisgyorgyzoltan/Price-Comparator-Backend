package com.codingchallenge.mapper;

import com.codingchallenge.dto.incoming.CreatePriceEntryDto;
import com.codingchallenge.dto.outgoing.GetPriceEntryDto;
import com.codingchallenge.model.PriceEntry;
import jakarta.validation.Valid;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceEntryMapper {
    GetPriceEntryDto toGetPriceEntryDto(PriceEntry priceEntry);

    @IterableMapping(elementTargetType = GetPriceEntryDto.class)
    List<GetPriceEntryDto> toGetPriceEntryDtos(List<PriceEntry> priceEntries);

    PriceEntry toPriceEntry(@Valid CreatePriceEntryDto createPriceEntryDto);
}
