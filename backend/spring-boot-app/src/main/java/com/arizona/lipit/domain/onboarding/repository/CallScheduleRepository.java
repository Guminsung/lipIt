package com.arizona.lipit.domain.onboarding.repository;

import com.arizona.lipit.domain.onboarding.entity.CallSchedule;
import com.arizona.lipit.domain.onboarding.entity.CallSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CallScheduleRepository extends JpaRepository<CallSchedule, Long> {

    Optional<CallSchedule> findByUserId(Long userId);
    Optional<CallSchedule> findByUserIdAndScheduledDay(Long userId, com.arizona.lipit.domain.onboarding.entity.DayOfWeek scheduledDay);
    boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);
}