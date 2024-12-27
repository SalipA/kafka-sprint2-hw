package ru.practicum.kafka_sprint_2.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class KafkaConfiguration {

    @Value("${KAFKA_BOOTSTRAP_SERVER}")
    private String kafkaBootStrapServer;

    @Value("${BLOCKED_USERS_TOPIC}")
    private String blockedUsersTopic;

    @Value("${ACKS_CONFIG}")
    private String producerAcks;

    @Value("${RETRIES_CONFIG}")
    private String producerRetriesConfig;
}
