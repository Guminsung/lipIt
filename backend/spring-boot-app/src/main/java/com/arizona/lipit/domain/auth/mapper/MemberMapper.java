package com.arizona.lipit.domain.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.arizona.lipit.domain.auth.dto.MemberDto;
import com.arizona.lipit.domain.auth.dto.SignupRequestDto;
import com.arizona.lipit.domain.auth.entity.Member;
import com.arizona.lipit.global.config.MapStructConfig;

@Mapper(config = MapStructConfig.class)
public interface MemberMapper {

	MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

	// Member → MemberDto 변환
	MemberDto toDto(Member member);

	// SignupRequestDto → Member 엔티티 변환
	@Mapping(source = "encodedPassword", target = "password")
	Member toEntity(SignupRequestDto request, String encodedPassword);
}
