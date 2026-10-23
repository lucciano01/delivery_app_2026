package br.edu.ppi.seller.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static br.edu.ppi.seller.constants.TopicConstants.ORDER_NOTIFICATION;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderNotificationTopic(){
        return TopicBuilder.name(ORDER_NOTIFICATION)
                .partitions(3)
                .replicas(1)
                .build();
    }

}
