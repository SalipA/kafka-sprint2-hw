package ru.practicum.kafka_sprint_2.dto;

import lombok.Data;

@Data
public class MessageDto {
    private String from;
    private String to;
    private String text;
}
