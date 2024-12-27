package ru.practicum.kafka_sprint_2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.kafka_sprint_2.dto.MessageDto;
import ru.practicum.kafka_sprint_2.dto.MessageEvent;
import ru.practicum.kafka_sprint_2.producer.MessageProducer;

import java.text.MessageFormat;
import java.time.LocalDateTime;

@Service
@Slf4j
public class MessageService {

    private final MessageProducer messageProducer;
    private final AdminService adminService;

    public MessageService(MessageProducer messageProducer, AdminService adminService) {
        this.messageProducer = messageProducer;
        this.adminService = adminService;
    }

    public void sendMessage (MessageDto messageDto) {
        if (adminService.isTopicExists(messageProducer.getTopicName())) {
            var event = new MessageEvent(messageDto.getFrom(), messageDto.getTo(), messageDto.getText(),
                LocalDateTime.now());
            var record = messageProducer.generateRecord(messageDto.getTo(), event);
            messageProducer.sendRecord(record);
        } else {
            log.warn(MessageFormat.format("Топик {0} не создан. Создайте топик и повторите попытку",
                messageProducer.getTopicName()));
        }
    }


}
