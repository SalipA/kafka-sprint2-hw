package ru.practicum.kafka_sprint_2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CensoredWordEvent {
    private String word;
}
