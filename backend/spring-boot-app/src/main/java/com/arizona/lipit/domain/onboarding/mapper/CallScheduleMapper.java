package com.arizona.lipit.domain.onboarding.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.arizona.lipit.domain.onboarding.dto.CallScheduleRequestDto;
import com.arizona.lipit.domain.onboarding.dto.CallScheduleResponseDto;
import com.arizona.lipit.domain.onboarding.entity.CallSchedule;
import com.arizona.lipit.domain.onboarding.entity.DayOfWeek;
import com.arizona.lipit.domain.onboarding.entity.TopicCategory;
import com.arizona.lipit.global.config.MapStructConfig;

@Mapper(config = MapStructConfig.class)
public interface CallScheduleMapper {

    CallScheduleMapper INSTANCE = Mappers.getMapper(CallScheduleMapper.class);

    // CallSchedule → CallScheduleResponseDto 변환
    @Mapping(target = "callScheduleId", source = "callScheduleId")
    @Mapping(target = "scheduledDay", expression = "java(callSchedule.getScheduledDay().name())")
    @Mapping(target = "topicCategory", expression = "java(callSchedule.getTopicCategory() != null ? callSchedule.getTopicCategory().name() : null)")
    CallScheduleResponseDto toDto(CallSchedule callSchedule);

    // 요청 DTO와 추가 정보로 Entity 생성
    @Mapping(target = "callScheduleId", ignore = true)
    CallSchedule toEntity(CallScheduleRequestDto requestDto, Long userId, DayOfWeek scheduledDay, TopicCategory topicCategory);
}