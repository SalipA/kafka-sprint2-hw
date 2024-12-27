package ru.practicum.kafka_sprint_2.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;
import ru.practicum.kafka_sprint_2.dto.MessageEvent;

import java.text.MessageFormat;


@Component
@Slf4j
public class MessageProcessor implements Processor<String, MessageEvent, String,
    MessageEvent> {
    private ProcessorContext<String, MessageEvent> context;
    private KeyValueStore<String, String> blockedUserStore;
    private KeyValueStore<String, String> censoredWordStore;


    @Override
    public void process(Record<String, MessageEvent> record) {
        log.info("Вызывается метод process процесора сообщений ...");
        MessageEvent message = record.value();
        String userTo = message.getRecipient_id();
        String userFrom = message.getUser_id();
        String text = message.getMessage();

        var blockedUsers = blockedUserStore.get(userTo);
        log.info(MessageFormat.format("Получено из хранилища пользоваталей: {0}", blockedUsers));
        if (blockedUsers != null && blockedUsers.contains(userFrom)) {
            log.info(MessageFormat.format("Пользователь {0} находится в списке заблокированных пользователей " +
                "пользователя {1}. Сообщение отправлено не будет.", userFrom, userTo));
            return;
        }

        var censoredWords = censoredWordStore.all();

        while (censoredWords.hasNext()) {
            String blockedWord = censoredWords.next().value;
            log.info(MessageFormat.format("Происходит проверка наличия в тексте сообщения слова {0} подлежащего " +
                "цензуре", blockedWord));
            if (text.contains(blockedWord)) {
                log.info(MessageFormat.format("Слово {0} находится в списке цензурированных. Будет заменено на ****!"
                    , blockedWord));
                text = text.replaceAll(blockedWord, "****");
                log.info(MessageFormat.format("Текущее значение text: {0}", text));
            } else {
                log.info(MessageFormat.format("Сообщение не содержит слово {0}",blockedWord));
        }
        }

        message.setMessage(text);
        context.forward(new Record<>(record.key(), message, record.timestamp()));
    }

    @Override
    public void init(ProcessorContext<String, MessageEvent> context) {
        this.context = context;
        this.censoredWordStore = context.getStateStore("censored-words-store");
        this.blockedUserStore = context.getStateStore("blocked-users-store");
    }

    @Override
    public void close() {
        Processor.super.close();
    }

}
