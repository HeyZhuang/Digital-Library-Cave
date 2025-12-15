package com.itzixi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息队列配置
 * 用于高并发场景下的消息异步处理
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    // 交换机名称
    public static final String ARTICLE_EXCHANGE = "article.exchange";
    public static final String COMMENT_EXCHANGE = "comment.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String STATS_EXCHANGE = "stats.exchange";

    // 队列名称
    public static final String ARTICLE_PUBLISH_QUEUE = "article.publish.queue";
    public static final String ARTICLE_UPDATE_QUEUE = "article.update.queue";
    public static final String COMMENT_PUBLISH_QUEUE = "comment.publish.queue";
    public static final String COMMENT_APPROVE_QUEUE = "comment.approve.queue";
    public static final String NOTIFICATION_EMAIL_QUEUE = "notification.email.queue";
    public static final String STATS_VISIT_QUEUE = "stats.visit.queue";
    public static final String STATS_SEARCH_QUEUE = "stats.search.queue";

    // 路由键
    public static final String ARTICLE_PUBLISH_ROUTING_KEY = "article.publish";
    public static final String ARTICLE_UPDATE_ROUTING_KEY = "article.update";
    public static final String COMMENT_PUBLISH_ROUTING_KEY = "comment.publish";
    public static final String COMMENT_APPROVE_ROUTING_KEY = "comment.approve";
    public static final String NOTIFICATION_EMAIL_ROUTING_KEY = "notification.email";
    public static final String STATS_VISIT_ROUTING_KEY = "stats.visit";
    public static final String STATS_SEARCH_ROUTING_KEY = "stats.search";

    /**
     * 消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate 配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        
        // 消息发送确认
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息发送成功: {}", correlationData);
            } else {
                log.error("消息发送失败: {}, 原因: {}", correlationData, cause);
            }
        });
        
        // 消息返回确认
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("消息被退回: exchange={}, routingKey={}, replyCode={}, replyText={}",
                    returned.getExchange(), returned.getRoutingKey(), 
                    returned.getReplyCode(), returned.getReplyText());
        });
        
        return rabbitTemplate;
    }

    /**
     * 文章交换机
     */
    @Bean
    public DirectExchange articleExchange() {
        return new DirectExchange(ARTICLE_EXCHANGE, true, false);
    }

    /**
     * 评论交换机
     */
    @Bean
    public DirectExchange commentExchange() {
        return new DirectExchange(COMMENT_EXCHANGE, true, false);
    }

    /**
     * 通知交换机
     */
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    /**
     * 统计交换机
     */
    @Bean
    public DirectExchange statsExchange() {
        return new DirectExchange(STATS_EXCHANGE, true, false);
    }

    /**
     * 文章发布队列
     */
    @Bean
    public Queue articlePublishQueue() {
        return QueueBuilder.durable(ARTICLE_PUBLISH_QUEUE)
                .withArgument("x-message-ttl", 30000) // 30秒过期
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "dlx.article.publish")
                .build();
    }

    /**
     * 文章更新队列
     */
    @Bean
    public Queue articleUpdateQueue() {
        return QueueBuilder.durable(ARTICLE_UPDATE_QUEUE)
                .withArgument("x-message-ttl", 30000)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "dlx.article.update")
                .build();
    }

    /**
     * 评论发布队列
     */
    @Bean
    public Queue commentPublishQueue() {
        return QueueBuilder.durable(COMMENT_PUBLISH_QUEUE)
                .withArgument("x-message-ttl", 30000)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "dlx.comment.publish")
                .build();
    }

    /**
     * 评论审核队列
     */
    @Bean
    public Queue commentApproveQueue() {
        return QueueBuilder.durable(COMMENT_APPROVE_QUEUE)
                .withArgument("x-message-ttl", 30000)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "dlx.comment.approve")
                .build();
    }

    /**
     * 邮件通知队列
     */
    @Bean
    public Queue notificationEmailQueue() {
        return QueueBuilder.durable(NOTIFICATION_EMAIL_QUEUE)
                .withArgument("x-message-ttl", 60000) // 60秒过期
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "dlx.notification.email")
                .build();
    }

    /**
     * 访问统计队列
     */
    @Bean
    public Queue statsVisitQueue() {
        return QueueBuilder.durable(STATS_VISIT_QUEUE)
                .withArgument("x-message-ttl", 15000) // 15秒过期
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "dlx.stats.visit")
                .build();
    }

    /**
     * 搜索统计队列
     */
    @Bean
    public Queue statsSearchQueue() {
        return QueueBuilder.durable(STATS_SEARCH_QUEUE)
                .withArgument("x-message-ttl", 15000)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "dlx.stats.search")
                .build();
    }

    /**
     * 绑定文章发布队列到交换机
     */
    @Bean
    public Binding articlePublishBinding() {
        return BindingBuilder.bind(articlePublishQueue())
                .to(articleExchange())
                .with(ARTICLE_PUBLISH_ROUTING_KEY);
    }

    /**
     * 绑定文章更新队列到交换机
     */
    @Bean
    public Binding articleUpdateBinding() {
        return BindingBuilder.bind(articleUpdateQueue())
                .to(articleExchange())
                .with(ARTICLE_UPDATE_ROUTING_KEY);
    }

    /**
     * 绑定评论发布队列到交换机
     */
    @Bean
    public Binding commentPublishBinding() {
        return BindingBuilder.bind(commentPublishQueue())
                .to(commentExchange())
                .with(COMMENT_PUBLISH_ROUTING_KEY);
    }

    /**
     * 绑定评论审核队列到交换机
     */
    @Bean
    public Binding commentApproveBinding() {
        return BindingBuilder.bind(commentApproveQueue())
                .to(commentExchange())
                .with(COMMENT_APPROVE_ROUTING_KEY);
    }

    /**
     * 绑定邮件通知队列到交换机
     */
    @Bean
    public Binding notificationEmailBinding() {
        return BindingBuilder.bind(notificationEmailQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_EMAIL_ROUTING_KEY);
    }

    /**
     * 绑定访问统计队列到交换机
     */
    @Bean
    public Binding statsVisitBinding() {
        return BindingBuilder.bind(statsVisitQueue())
                .to(statsExchange())
                .with(STATS_VISIT_ROUTING_KEY);
    }

    /**
     * 绑定搜索统计队列到交换机
     */
    @Bean
    public Binding statsSearchBinding() {
        return BindingBuilder.bind(statsSearchQueue())
                .to(statsExchange())
                .with(STATS_SEARCH_ROUTING_KEY);
    }
} 