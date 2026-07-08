package com.example.agrilinkback.module.consultation.entity;

public record Reserve(
        Integer id,
        String expertName,
        String questioner,
        String area,
        String address,
        String plantName,
        String soilCondition,
        String plantCondition,
        String plantDetail,
        String phone,
        String message,
        String answer,
        Integer status,
        String appointmentTime,
        String serviceMode
) {
}
