package ru.practicum.kafka_sprint_2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageEvent {
    private String user_id;
    private String recipient_id;
    private String message;
    private LocalDateTime timestamp;
}
