package com.schoollaboratory.model;

public record Equipment(
        int equipmentId,
        String equipmentName,
        String category,
        String location,
        String conditionStatus
) {
}
