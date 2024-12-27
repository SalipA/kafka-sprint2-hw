package ru.practicum.kafka_sprint_2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlockingUserEvent {
    private String blockingInitiatorId;
    private String blockedUserId;
    private boolean isBlocked;

    @Override
    public String toString() {
        return "BlockingUserEvent{" +
            "blockingInitiatorId='" + blockingInitiatorId + '\'' +
            ", blockedUserId='" + blockedUserId + '\'' +
            ", isBlocked=" + isBlocked +
            '}';
    }
}
