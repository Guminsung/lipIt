package com.arizona.lipit.domain.onboarding.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "call_schedule")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long callScheduleId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek scheduledDay;

    @Column(nullable = false)
    private String scheduledTime;

    @Enumerated(EnumType.STRING)
    private TopicCategory topicCategory;
}