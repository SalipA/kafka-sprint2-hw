package ru.practicum.kafka_sprint_2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.kafka_sprint_2.dto.BlockingUserDto;
import ru.practicum.kafka_sprint_2.service.BlockingUserService;

@RestController
@RequestMapping("/block")
public class BlockingUserController {
    private final BlockingUserService blockingUserService;
    private final static String BLOCKED = "BLOCKED";
    private final static String UNBLOCKED = "UNBLOCKED";

    public BlockingUserController(BlockingUserService blockingUserService) {
        this.blockingUserService = blockingUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String blockUser(@RequestBody BlockingUserDto blockingUserDto) {
        blockingUserService.blockUser(blockingUserDto);
        return BLOCKED;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public String unblockUser(@RequestBody BlockingUserDto blockingUserDto) {
        blockingUserService.unblockUser(blockingUserDto);
        return UNBLOCKED;
    }
}
