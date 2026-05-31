package com.nxtwave.dto;

public interface UserAnalyticsProjection {
    String getUsername();
    Long getOverdueCount();
    Double getAvgCompletionTimeHours();
}