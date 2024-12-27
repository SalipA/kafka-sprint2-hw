package ru.practicum.kafka_sprint_2.producer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;
import ru.practicum.kafka_sprint_2.config.KafkaConfiguration;
import ru.practicum.kafka_sprint_2.dto.MessageEvent;

import java.text.MessageFormat;
import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Component
@Slf4j
public class MessageProducer {
    private KafkaProducer<String, MessageEvent> producer;
    private final KafkaConfiguration producerConf;

    public MessageProducer(KafkaConfiguration producerConf) {
        this.producerConf = producerConf;
    }

    @PostConstruct
    public void initProducer() {
        this.producer = new KafkaProducer<>(getKafkaBrokerProperties());
    }

    private Properties getKafkaBrokerProperties() {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, producerConf.getKafkaBootStrapServer());
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        properties.put(ACKS_CONFIG, producerConf.getProducerAcks());
        properties.put(RETRIES_CONFIG, producerConf.getProducerRetriesConfig());
        return properties;
    }

    public ProducerRecord<String, MessageEvent> generateRecord(String userToId,
                                                               MessageEvent messageEvent) {
        return new ProducerRecord<>(producerConf.getMessagesTopic(), userToId, messageEvent);
    }

    public void sendRecord(ProducerRecord<String, MessageEvent> record) {
        log.info(
            MessageFormat.format("Продюсер сообщений отправляет запись в топик. Key: {0}. Value: {1}",
                record.key(), record.value()));
        producer.send(record,(metadata, exception) -> {
            if (exception != null) {
                log.error("Получена ошибка: " + exception.getMessage());
                producer.close();
            }
        });
    }

    public String getTopicName() {
        return producerConf.getMessagesTopic();
    }
}
