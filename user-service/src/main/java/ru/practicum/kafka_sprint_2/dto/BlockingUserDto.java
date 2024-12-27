package ru.practicum.kafka_sprint_2.dto;

import lombok.Data;

@Data
public class BlockingUserDto {
    private String blockingInitiatorId;
    private String blockedUserId;

    @Override
    public String toString() {
        return "BlockingUserEvent{" +
            "blockingInitiatorId='" + blockingInitiatorId + '\'' +
            ", blockedUserId='" + blockedUserId + '\'' +
            '}';
    }
}
