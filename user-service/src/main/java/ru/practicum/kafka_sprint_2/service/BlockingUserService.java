package ru.practicum.kafka_sprint_2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.kafka_sprint_2.dto.BlockingUserDto;
import ru.practicum.kafka_sprint_2.dto.BlockingUserEvent;
import ru.practicum.kafka_sprint_2.producer.BlockingUserEventProducer;

import java.text.MessageFormat;

@Service
@Slf4j
public class BlockingUserService {
    private final BlockingUserEventProducer producer;
    private final AdminService adminService;

    public BlockingUserService(BlockingUserEventProducer producer, AdminService adminService) {
        this.producer = producer;
        this.adminService = adminService;
    }

    public void blockUser(BlockingUserDto blockingUserDto) {
        if (adminService.isTopicExists(producer.getTopicName())) {
            var event = new BlockingUserEvent(blockingUserDto.getBlockingInitiatorId(),
                blockingUserDto.getBlockedUserId(), true);
            var record = producer.generateRecord(event.getBlockingInitiatorId(), event);
            producer.sendRecord(record);
        } else {
            log.warn(MessageFormat.format("Топик {0} не создан. Создайте топик и повторите попытку",
                producer.getTopicName()));
        }
    }

    public void unblockUser(BlockingUserDto blockingUserDto) {
        if (adminService.isTopicExists(producer.getTopicName())) {
            var event = new BlockingUserEvent(blockingUserDto.getBlockingInitiatorId(),
                blockingUserDto.getBlockedUserId(), false);
            var record = producer.generateRecord(event.getBlockingInitiatorId(), event);
            producer.sendRecord(record);
        } else {
            log.warn(MessageFormat.format("Топик {0} не создан. Создайте топик и повторите попытку",
                producer.getTopicName()));
        }
    }
}
