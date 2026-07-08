package com.example.agrilinkback.module.consultation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;

public record Question(
        Integer id,
        String expertName,
        String questioner,
        String phone,
        String plantName,
        String title,
        String question,
        String answer,
        Integer status,
        @JsonIgnore String attachmentsText
) {
    @JsonProperty("attachments")
    public List<String> attachments() {
        if (attachmentsText == null || attachmentsText.isBlank()) {
            return List.of();
        }
        return Arrays.stream(attachmentsText.split("\\R"))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }
}
