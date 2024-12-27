package ru.practicum.kafka_sprint_2.producer;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;
import ru.practicum.kafka_sprint_2.config.KafkaConfiguration;
import ru.practicum.kafka_sprint_2.dto.CensoredWordEvent;

import java.text.MessageFormat;
import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Component
@Data
@Slf4j
public class CensoredWordProducer {
    private KafkaProducer<String, CensoredWordEvent> producer;
    private final KafkaConfiguration producerConf;

    public CensoredWordProducer(KafkaConfiguration producerConf) {
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

    public ProducerRecord<String, CensoredWordEvent> generateRecord(String eventId, CensoredWordEvent censoredWordEvent) {
        return new ProducerRecord<>(producerConf.getCensoredWordsTopic(), eventId, censoredWordEvent);
    }

    public void sendRecord(ProducerRecord<String, CensoredWordEvent> record) {
        log.info(
            MessageFormat.format("Продюсер цензурированных слов отправляет запись в топик. Key: {0}. Value: {1}",
                record.key(), record.value()));
        producer.send(record,(metadata, exception) -> {
            if (exception != null) {
                System.err.println("Получена ошибка: " + exception.getMessage());
                producer.close();
            }
        });
    }

    public String getTopicName() {
        return producerConf.getCensoredWordsTopic();
    }



}
