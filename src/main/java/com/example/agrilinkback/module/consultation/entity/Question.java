package com.example.agrilinkback.module.consultation.entity;

public record Question(
        Integer id,
        String expertName,
        String questioner,
        String phone,
        String plantName,
        String title,
        String question,
        String answer,
        Integer status
) {
}
