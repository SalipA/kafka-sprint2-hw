package ru.practicum.kafka_sprint_2.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;
import ru.practicum.kafka_sprint_2.dto.BlockingUserEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Component
@Slf4j
public class BlockingUserStatefulProcessor implements Processor<String, BlockingUserEvent, Void,
    Void> {
    private ProcessorContext<Void, Void> context;
    private KeyValueStore<String, String> stateStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Record<String, BlockingUserEvent> record) {
        log.info(MessageFormat.format("Получено сообщение об измненении списка заблокированных пользователей для " +
            "пользователя {0}", record.key()));
        String blockingInitiatorId = record.key();
        List<String> blockedUsersIdsList = getBlockedUserListForInitiator(blockingInitiatorId);
        blockedUsersIdsList = updateBlockedUserListForInitiator(blockedUsersIdsList, record.value());
        saveUpdates(blockedUsersIdsList, blockingInitiatorId);
    }

    @Override
    public void init(ProcessorContext<Void, Void> context) {
        this.context = context;
        this.stateStore = context.getStateStore("blocked-users-store");
    }

    @Override
    public void close() {
        Processor.super.close();
    }

    private List<String> getBlockedUserListForInitiator(String blockingInitiatorId) {
        log.info(MessageFormat.format("Получаю список заблокированных пользователей для {0}", blockingInitiatorId));
        String blockedUsersIdsString = stateStore.get(blockingInitiatorId);
        List<String> blockedUsersIdsList = new ArrayList<>();
        try {
            if (blockedUsersIdsString != null)
                blockedUsersIdsList = new ArrayList<> (Arrays.asList(objectMapper.readValue(blockedUsersIdsString,
            String[].class)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return blockedUsersIdsList;
    }

    private List<String> updateBlockedUserListForInitiator(List<String> blockedUsersIdsList, BlockingUserEvent blockingUserEvent) {
        String blockedUserId = blockingUserEvent.getBlockedUserId();
        boolean isBlocked = blockingUserEvent.isBlocked();
        if (blockedUsersIdsList.contains(blockedUserId) && isBlocked) {
            log.info(MessageFormat.format("Пользователь с идентификатором {0} уже находится в списке заблокированных " +
                "для пользователя {1} и повторно добавляться не будет", blockedUserId, blockingUserEvent.getBlockingInitiatorId()));
        } else if (blockedUsersIdsList.contains(blockedUserId) && !isBlocked) {
            log.info(MessageFormat.format("Получено сообщение о разблокировке пользователя с идентификатором {0} для пользователя {1}.Разблокировка будет произведена",blockedUserId, blockingUserEvent.getBlockingInitiatorId()));
            blockedUsersIdsList.remove(blockedUserId);
        } else if (!blockedUsersIdsList.contains(blockedUserId) && isBlocked) {
            log.info(MessageFormat.format("Получено сообщение о добавлении пользователя с идентификатором {0} в список заблокированных для пользователя {1}.Блокировка будет произведена",blockedUserId,blockingUserEvent.getBlockingInitiatorId()));
            blockedUsersIdsList.add(blockedUserId);
        } else if (!blockedUsersIdsList.contains(blockedUserId) && !isBlocked) {
            log.info(MessageFormat.format("Получено сообщение о разблокировке пользователя с идентификатором {0} для " +
                "пользователя {1} Данный пользователь не был заблокирован ранее. Разблокировка не нужна",blockedUserId, blockingUserEvent.getBlockingInitiatorId()));
        }
        return blockedUsersIdsList;
    }

    private void saveUpdates(List<String> blockedUsersIdsList, String blockingInitiatorId) {
        try {
            if (blockedUsersIdsList.isEmpty()) {
                log.info(MessageFormat.format("Список заблокированных пользователей для пользователя пустой. Ключ будет удален из хранилища", blockingInitiatorId));
                stateStore.delete(blockingInitiatorId);
            } else {
                String updatedList = objectMapper.writeValueAsString(blockedUsersIdsList);
                log.info(MessageFormat.format("Список заблокированных пользователей для пользователя {0}: {1}. Запись" +
                    " хранилища будет обновлена.",blockingInitiatorId , updatedList));
                stateStore.put(blockingInitiatorId, updatedList);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
