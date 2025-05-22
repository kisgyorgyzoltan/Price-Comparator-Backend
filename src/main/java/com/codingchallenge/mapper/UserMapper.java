package com.codingchallenge.mapper;

import com.codingchallenge.dto.incoming.CreateUserDto;
import com.codingchallenge.dto.incoming.UpdateUserDto;
import com.codingchallenge.dto.outgoing.GetUserDto;
import com.codingchallenge.model.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public abstract class UserMapper {
    public abstract GetUserDto toGetUserDto(User user);

    public abstract User toUser(CreateUserDto createUserDto);


    @IterableMapping(elementTargetType = GetUserDto.class)
    public abstract List<GetUserDto> toGetUserDtos(List<User> users);
}
