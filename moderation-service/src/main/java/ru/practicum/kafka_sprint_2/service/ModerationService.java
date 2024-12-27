package ru.practicum.kafka_sprint_2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.kafka_sprint_2.dto.CensoredWordEvent;
import ru.practicum.kafka_sprint_2.producer.CensoredWordProducer;

import java.text.MessageFormat;


@Service
@Slf4j
public class ModerationService {
    private final CensoredWordProducer censoredWordProducer;
    private final AdminService adminService;

    public ModerationService(CensoredWordProducer censoredWordProducer, AdminService adminService){
        this.censoredWordProducer = censoredWordProducer;
        this.adminService = adminService;
    }
    public void addCensoredWord(CensoredWordEvent censoredWord) {
        if (adminService.isTopicExists(censoredWordProducer.getTopicName())) {
                var record = censoredWordProducer.generateRecord(censoredWord.getWord(), censoredWord);
                censoredWordProducer.sendRecord(record);
        } else {
            log.warn(MessageFormat.format("Топик {0} не создан. Создайте топик и повторите попытку",
                censoredWordProducer.getTopicName()));
        }
    }

    public void removeCensoredWord(CensoredWordEvent censoredWord) {
        if (adminService.isTopicExists(censoredWordProducer.getTopicName())) {
            var record = censoredWordProducer.generateRecord(censoredWord.getWord(), null);
            censoredWordProducer.sendRecord(record);
        } else {
            log.warn(MessageFormat.format("Топик {0} не создан. Создайте топик и повторите попытку",
                censoredWordProducer.getTopicName()));
        }
    }


}
