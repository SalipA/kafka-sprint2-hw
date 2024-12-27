package ru.practicum.kafka_sprint_2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.kafka_sprint_2.dto.CensoredWordDto;
import ru.practicum.kafka_sprint_2.dto.CensoredWordEvent;
import ru.practicum.kafka_sprint_2.service.ModerationService;

@RestController
@RequestMapping("/censored")
public class CensoredWordController {

    private final ModerationService moderationService;
    private final static String ADDED = "ADDED";
    private final static String DELETED = "DELETED";

    public CensoredWordController(ModerationService moderationService) {
        this.moderationService = moderationService;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String addCensoredWord(@RequestBody CensoredWordEvent censoredWord) {
        moderationService.addCensoredWord(censoredWord);
        return "ADDED";
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public String removeCensoredWord(@RequestBody CensoredWordEvent censoredWord) {
        moderationService.removeCensoredWord(censoredWord);
        return "DELETED";
    }


}
