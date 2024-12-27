package ru.practicum.kafka_sprint_2.service;

import jakarta.annotation.PostConstruct;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.stereotype.Service;
import ru.practicum.kafka_sprint_2.config.KafkaConfiguration;

import java.util.Properties;

@Service
public class AdminService {
    private final KafkaConfiguration kafkaConfiguration;
    private AdminClient adminClient;


    public AdminService(KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
    }

    @PostConstruct
    public void initAdminClient() {
        this.adminClient = AdminClient.create(getKafkaBrokerProperties());
    }

    private Properties getKafkaBrokerProperties() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getKafkaBootStrapServer());
        return properties;
    }


    public boolean isTopicExists(String topicName) {
        try {
            return adminClient.listTopics().names().get().contains(topicName);
        } catch (Exception e) {
            throw new RuntimeException("Получена ошибка при проверке наличия топика: " + e.getMessage());
        }
    }
}
