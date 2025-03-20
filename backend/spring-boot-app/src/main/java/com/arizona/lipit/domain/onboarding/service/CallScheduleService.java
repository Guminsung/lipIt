package com.arizona.lipit.domain.onboarding.service;

import com.arizona.lipit.domain.onboarding.dto.CallScheduleRequestDto;
import com.arizona.lipit.domain.onboarding.dto.CallScheduleResponseDto;
import com.arizona.lipit.domain.onboarding.entity.CallSchedule;
import com.arizona.lipit.domain.onboarding.entity.DayOfWeek;
import com.arizona.lipit.domain.onboarding.entity.TopicCategory;
import com.arizona.lipit.domain.onboarding.mapper.CallScheduleMapper;
import com.arizona.lipit.domain.onboarding.repository.CallScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CallScheduleService {
    private final CallScheduleRepository callScheduleRepository;

    @Transactional
    public CallScheduleResponseDto saveCallSchedule(Long userId, CallScheduleRequestDto requestDto) {
        // Enum 변환
        DayOfWeek scheduledDay = DayOfWeek.valueOf(requestDto.getScheduledDay());
        TopicCategory topicCategory = requestDto.getTopicCategory() != null ?
                TopicCategory.valueOf(requestDto.getTopicCategory()) : null;

        // 기존 알림 일정이 있으면 업데이트, 없으면 생성
        CallSchedule callSchedule = callScheduleRepository.findByUserId(userId)
                .map(existingSchedule -> {
                    // 기존 엔티티 업데이트 (매퍼 사용 안함)
                    existingSchedule.setScheduledDay(scheduledDay);
                    existingSchedule.setScheduledTime(requestDto.getScheduledTime());
                    existingSchedule.setTopicCategory(topicCategory);
                    return existingSchedule;
                })
                .orElseGet(() -> {
                    // 새 엔티티 생성 (매퍼 사용)
                    return CallScheduleMapper.INSTANCE.toEntity(requestDto, userId, scheduledDay, topicCategory);
                });

        // 저장
        callSchedule = callScheduleRepository.save(callSchedule);

        // 매퍼를 사용하여 DTO 변환 및 반환
        return CallScheduleMapper.INSTANCE.toDto(callSchedule);
    }

    public CallScheduleResponseDto getCallSchedule(Long userId) {
        return callScheduleRepository.findByUserId(userId)
                .map(CallScheduleMapper.INSTANCE::toDto)  // 매퍼 사용
                .orElse(null);
    }

    @Transactional
    public boolean deleteCallSchedule(Long userId) {
        if (callScheduleRepository.existsByUserId(userId)) {
            callScheduleRepository.deleteByUserId(userId);
            return true;
        }
        return false;
    }
}