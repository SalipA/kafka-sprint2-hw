package ru.practicum.kafka_sprint_2.dto;

import lombok.Data;

@Data
public class CensoredWordEvent {
    private String id;
    private String word;

    @Override
    public String toString() {
        return "CensoredWordEvent{" +
            "id='" + id + '\'' +
            ", word='" + word + '\'' +
            '}';
    }
}
