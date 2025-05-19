package com.codingchallenge.mapper;

import com.codingchallenge.dto.incoming.CreatePriceAlertDto;
import com.codingchallenge.dto.outgoing.GetPriceAlertDto;
import com.codingchallenge.model.PriceAlert;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceAlertMapper {
    GetPriceAlertDto toGetPriceAlertDto(PriceAlert priceAlert);

    @IterableMapping(elementTargetType = GetPriceAlertDto.class)
    List<GetPriceAlertDto> toGetPriceAlertDtos(List<PriceAlert> priceAlerts);

    PriceAlert toPriceAlert(CreatePriceAlertDto createPriceAlertDto);
}
