package ru.practicum.kafka_sprint_2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.kafka_sprint_2.dto.MessageDto;
import ru.practicum.kafka_sprint_2.service.MessageService;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;
    private final String SENT = "SENT";

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String sendMessage(@RequestBody MessageDto messageDto) {
        messageService.sendMessage(messageDto);
        return SENT;
    }

}
