package com.arizona.lipit.domain.onboarding.entity;

public enum TopicCategory {
    SPORTS, TRIP, MOVIE, FOOD, GAME, MUSIC, HEALTH;

    public static boolean isValid(String category) {
        if (category == null) return true;

        try {
            TopicCategory.valueOf(category);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}