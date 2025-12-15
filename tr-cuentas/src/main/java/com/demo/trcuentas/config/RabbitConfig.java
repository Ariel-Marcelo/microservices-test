package com.demo.trcuentas.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String CREATE_QUEUE = "cliente.created.queue";
    public static final String UPDATE_QUEUE = "cliente.updated.queue";
    public static final String DELETE_QUEUE = "cliente.deleted.queue";
    public static final String EXCHANGE_NAME = "bank.exchange";
    public static final String ROUTING_KEY_CREATED = "cliente.created";
    public static final String ROUTING_KEY_UPDATED = "cliente.updated";
    public static final String ROUTING_KEY_DELETED = "cliente.deleted";
    public static final String DLQ_NAME = "bank.global.dlq";
    public static final String DLQ_ROUTING_KEY = "dead.letter";

    @Bean
    public Queue globalDlq() {
        return new Queue(DLQ_NAME, true);
    }

    @Bean
    public Queue createQueue() {
        return QueueBuilder.durable(CREATE_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue updateQueue() {
        return QueueBuilder.durable(UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue deleteQueue() {
        return QueueBuilder.durable(DELETE_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding createBinding(Queue createQueue, TopicExchange exchange) {
        return BindingBuilder.bind(createQueue).to(exchange).with(ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding updateBinding(Queue updateQueue, TopicExchange exchange) {
        return BindingBuilder.bind(updateQueue).to(exchange).with(ROUTING_KEY_UPDATED);
    }

    @Bean
    public Binding deleteBinding(Queue deleteQueue, TopicExchange exchange) {
        return BindingBuilder.bind(deleteQueue).to(exchange).with(ROUTING_KEY_DELETED);
    }

    @Bean
    public Binding dlqBinding(Queue globalDlq, TopicExchange exchange) {
        return BindingBuilder.bind(globalDlq).to(exchange).with(DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}