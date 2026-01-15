package com.runing.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.runing.user.dto.ProfileDto;
import com.runing.user.entity.User;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
	@Mapping(source = "email", target = "email")
	@Mapping(source = "profile.name", target = "name")
	@Mapping(source = "profile.nickname", target = "nickname")
	@Mapping(source = "profile.phoneNumber", target = "phoneNumber")
	@Mapping(source = "profile.profileUrl", target = "profileUrl")
	@Mapping(source = "profile.region", target = "region")
	ProfileDto toDto(User user);
}
