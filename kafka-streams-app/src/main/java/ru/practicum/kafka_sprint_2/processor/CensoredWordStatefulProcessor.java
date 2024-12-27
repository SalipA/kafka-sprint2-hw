package ru.practicum.kafka_sprint_2.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;
import ru.practicum.kafka_sprint_2.dto.CensoredWordEvent;

import java.text.MessageFormat;

@Component
@Slf4j
public class CensoredWordStatefulProcessor implements Processor<String, CensoredWordEvent, Void,
    Void> {

    private ProcessorContext<Void, Void> context;
    private KeyValueStore<String, String> stateStore;


    @Override
    public void process(Record<String, CensoredWordEvent> record) {
        String key = record.key();
        CensoredWordEvent value = record.value();
        String censoredWord = stateStore.get(key);
            if (censoredWord == null && value != null) {
                log.info(MessageFormat.format("Слова {0} нет в спике заблокированных. Будет добавлено!",
                    value.getWord()));
                stateStore.put(key, value.getWord());
            } else if (censoredWord != null && value == null){
                log.info(MessageFormat.format("Слово {0} было в списке заблокированных. Будет удалено!", censoredWord));
                stateStore.delete(key);
            } else {
                log.info(MessageFormat.format("Слово {0} уже есть в списке заблокированных. Повторно добавляться не будет!", censoredWord));
            }
        context.commit();
    }

    @Override
    public void init(ProcessorContext<Void, Void> context) {
        this.context = context;
        this.stateStore = context.getStateStore("censored-words-store");
    }

    @Override
    public void close() {
        Processor.super.close();
    }
}
