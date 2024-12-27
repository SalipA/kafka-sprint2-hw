package ru.practicum.kafka_sprint_2.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class KafkaStreamsConfiguration {
    @Value("${KAFKA_BOOTSTRAP_SERVER}")
    private String kafkaBootStrapServer;

    @Value("${BLOCKED_USERS_TOPIC}")
    private String blockedUsersTopic;

    @Value("${CENSORED_WORDS_TOPIC}")
    private String censoredWordsTopic;

    @Value("${MESSAGES_TOPIC}")
    private String messagesTopic;

    @Value("${FILTERED_MESSAGES_TOPIC}")
    private String filteredMessagesTopic;

    @Value("${APPLICATION_ID}")
    private String applicationId;
}
