package ru.practicum.kafka_sprint_2.processor;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;
import ru.practicum.kafka_sprint_2.AdminService;
import ru.practicum.kafka_sprint_2.config.KafkaStreamsConfiguration;
import ru.practicum.kafka_sprint_2.dto.BlockingUserEvent;
import ru.practicum.kafka_sprint_2.dto.CensoredWordEvent;
import ru.practicum.kafka_sprint_2.dto.MessageEvent;


import java.util.List;
import java.util.Properties;


@Component
public class MainProcessorApp {
    private final KafkaStreamsConfiguration configuration;
    private final JsonSerde<BlockingUserEvent> blockingUserEventJsonSerde = new JsonSerde<>(BlockingUserEvent.class);
    private final JsonSerde<MessageEvent> messageEventJsonSerde = new JsonSerde<>(MessageEvent.class);
    private final JsonSerde<CensoredWordEvent> censoredWordEventJsonSerde = new JsonSerde<>(CensoredWordEvent.class);

    private final AdminService adminService;

    public MainProcessorApp(KafkaStreamsConfiguration kafkaStreamsConfiguration, AdminService adminService) {
        this.configuration = kafkaStreamsConfiguration;
        this.adminService = adminService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        adminService.waitForTopics(List.of(configuration.getBlockedUsersTopic(), configuration.getMessagesTopic(),
                configuration.getMessagesTopic(), configuration.getFilteredMessagesTopic()),
            60000);

        StoreBuilder blockedUsersStore = Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore("blocked-users-store"),
            Serdes.String(),
            Serdes.String()
        ).withLoggingDisabled();

        StoreBuilder censoredWordStore = Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore("censored-words-store"),
            Serdes.String(),
            Serdes.String()
        ).withLoggingDisabled();

        Topology topology = new Topology();

        topology.addSource("Source1", Serdes.String().deserializer(), messageEventJsonSerde.deserializer(), "messages");

        topology.addProcessor("MessageProcessor", () -> new MessageProcessor(), "Source1");

        topology.addSink(
            "FilteredMessageSink",
            "filtered_messages",
            Serdes.String().serializer(),
            messageEventJsonSerde.serializer(),
            "MessageProcessor"
        );

        topology.addGlobalStore(
            censoredWordStore,
            "Source2",
            Serdes.String().deserializer(),
            censoredWordEventJsonSerde.deserializer(),
            configuration.getCensoredWordsTopic(),
            "CensoredWordStatefulProcessor",
            () -> new CensoredWordStatefulProcessor()
        );

        topology.addGlobalStore(
            blockedUsersStore,
            "Source3",
            Serdes.String().deserializer(),
            blockingUserEventJsonSerde.deserializer(),
            configuration.getBlockedUsersTopic(),
            "BlockingUserStatefulProcessor",
            () -> new BlockingUserStatefulProcessor()
        );

        KafkaStreams streams = new KafkaStreams(topology, getKafkaBrokerProperties());
        streams.start();
    }

    private Properties getKafkaBrokerProperties() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, configuration.getApplicationId());
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getKafkaBootStrapServer());
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return properties;
    }
}
