package com.arizona.lipit.domain.onboarding.entity;

public enum DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    public static boolean isValid(String day) {
        try {
            DayOfWeek.valueOf(day);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}