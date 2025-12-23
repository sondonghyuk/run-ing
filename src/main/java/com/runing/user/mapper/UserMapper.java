package com.runing.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.runing.user.dto.UserDto;
import com.runing.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "userId",source = "uuid")
	UserDto toDto(User user);
}
